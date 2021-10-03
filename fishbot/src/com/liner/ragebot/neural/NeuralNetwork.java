package com.liner.ragebot.neural;

import com.google.gson.Gson;
import com.liner.ragebot.utils.Files;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class NeuralNetwork {
    private final double learningRate;
    private Layer[] layers;
    private final UnaryOperator<Double> activation;
    private final UnaryOperator<Double> derivative;

    public NeuralNetwork(double learningRate, int... layers) {
        this.learningRate = learningRate;
        this.activation = x -> 1 / (1 + Math.exp(-x));
        this.derivative = y -> y * (1 - y);
        initRandomWeight(layers);
    }

    public NeuralNetwork(File file) {
        this.learningRate = 0.001;
        this.activation = x -> 1 / (1 + Math.exp(-x));
        this.derivative = y -> y * (1 - y);
        layers = new Gson().fromJson(Files.readFile(file), NeuralNetworkData.class).getLayers();
    }

    private double[] feedForward(double[] inputs) {
        System.arraycopy(inputs, 0, layers[0].neurons, 0, inputs.length);
        for (int i = 1; i < layers.length; i++) {
            Layer l = layers[i - 1];
            Layer l1 = layers[i];
            for (int j = 0; j < l1.size; j++) {
                l1.neurons[j] = 0;
                for (int k = 0; k < l.size; k++) {
                    l1.neurons[j] += l.neurons[k] * l.weights[k][j];
                }
                l1.neurons[j] += l1.biases[j];
                l1.neurons[j] = activation.apply(l1.neurons[j]);
            }
        }
        return layers[layers.length - 1].neurons;
    }

    private void backpropagation(double[] targets) {
        double[] errors = new double[layers[layers.length - 1].size];
        for (int i = 0; i < layers[layers.length - 1].size; i++) {
            errors[i] = targets[i] - layers[layers.length - 1].neurons[i];
        }
        for (int k = layers.length - 2; k >= 0; k--) {
            Layer l = layers[k];
            Layer l1 = layers[k + 1];
            double[] errorsNext = new double[l.size];
            double[] gradients = new double[l1.size];
            for (int i = 0; i < l1.size; i++) {
                gradients[i] = errors[i] * derivative.apply(layers[k + 1].neurons[i]);
                gradients[i] *= learningRate;
            }
            double[][] deltas = new double[l1.size][l.size];
            for (int i = 0; i < l1.size; i++) {
                for (int j = 0; j < l.size; j++) {
                    deltas[i][j] = gradients[i] * l.neurons[j];
                }
            }
            for (int i = 0; i < l.size; i++) {
                errorsNext[i] = 0;
                for (int j = 0; j < l1.size; j++) {
                    errorsNext[i] += l.weights[i][j] * errors[j];
                }
            }
            errors = new double[l.size];
            System.arraycopy(errorsNext, 0, errors, 0, l.size);
            double[][] weightsNew = new double[l.weights.length][l.weights[0].length];
            for (int i = 0; i < l1.size; i++) {
                for (int j = 0; j < l.size; j++) {
                    weightsNew[j][i] = l.weights[j][i] + deltas[i][j];
                }
            }
            l.weights = weightsNew;
            for (int i = 0; i < l1.size; i++) {
                l1.biases[i] += gradients[i];
            }
        }
    }


    public void save(File file) {
        Files.writeFile(file, new Gson().toJson(new NeuralNetworkData(layers)));
    }

    public static NeuralNetwork load(File file) {
        return new NeuralNetwork(file);
    }

    private List<Digit> getAllDigits(List<Digits> digitsList){
        List<Digit> digits = new ArrayList<>();
        for(Digits captcha: digitsList){
            digits.addAll(captcha.getDigitList());
        }
        return digits;
    }

    public void learn(int epochs, int batchSize, List<Digits> digitsList, Callback callback) {
        List<Digit> digits = getAllDigits(digitsList);
        for (int epoch = 0; epoch < epochs; epoch++) {
            int correct = 0;
            for (int i = 0; i < batchSize; i++) {
                int index = (int) (Math.random() * (batchSize));
                Digit digit = digits.get(index);
                double[] targetNeurons = new double[10];
                targetNeurons[digit.getAnswer()] = 1;
                if (predictDigit(digit) == digit.getAnswer()) {
                    correct++;
                }
                backpropagation(targetNeurons);
            }
            if (callback != null) {
                callback.onLearnEpoch(epoch, epochs,  batchSize, correct);
            }
        }
    }

    public int predict(Digits digits) {
        StringBuilder result = new StringBuilder();
        for(Digit digit: digits.getDigitList()){
            result.append(predictDigit(digit));
        }
       return Integer.parseInt(result.toString());
    }

    public int predictDigit(Digit digit) {
        double[] outputs = feedForward(digit.getInput());
        int predicted = 0;
        double weight = -1;
        for (int j = 0; j < 10; j++)
            if (outputs[j] > weight) {
                weight = outputs[j];
                predicted = j;
            }
        return predicted;
    }

    private void initRandomWeight(int... sizes) {
        layers = new Layer[sizes.length];
        for (int i = 0; i < sizes.length; i++) {
            int nextSize = 0;
            if (i < sizes.length - 1) nextSize = sizes[i + 1];
            layers[i] = new Layer(sizes[i], nextSize);
            for (int j = 0; j < sizes[i]; j++) {
                layers[i].biases[j] = Math.random() * 2.0 - 1.0;
                for (int k = 0; k < nextSize; k++) {
                    layers[i].weights[j][k] = Math.random() * 2.0 - 1.0;
                }
            }
        }
    }

    public interface Callback {
        void onLearnEpoch(int epoch, int totalEpochs, int totalBatch, int correctAnswers);
        void onFinished();
    }
}

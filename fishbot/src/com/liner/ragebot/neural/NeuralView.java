package com.liner.ragebot.neural;


import com.liner.ragebot.ui.DrawGraph;
import com.liner.ragebot.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class NeuralView {
    private static JFrame frame;
    private NeuralNetwork neuralNetwork;
    private int epochSizeValue = 100;
    private int generateCountValue = 1000;
    private int batchSizeValue = 100;


    private JPanel panel;
    private DrawGraph graph;
    private JButton startButton;
    private JTextField epochEdit;
    private JTextField generateCount;
    private JPanel graphHolder;
    private JLabel statusText;
    private JLabel trainView;
    private JButton start2;
    private List<Digits> digitsList;


    public NeuralView() {
        digitsList = new ArrayList<>();
        neuralNetwork = new NeuralNetwork(0.0001, 150, 64, 32, 16, 10);
        graph = new DrawGraph();
        graphHolder.setLayout(new BorderLayout());
        graphHolder.add(graph, BorderLayout.CENTER);

start2.addActionListener(new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                epochSizeValue = Integer.parseInt(epochEdit.getText().toString());
                generateCountValue = Integer.parseInt(generateCount.getText().toString());
                digitsList = new ArrayList<>();
                for(File file: Objects.requireNonNull(new File(System.getProperty("user.dir"), "screenshots").listFiles()))
                    digitsList.add(new Digits(file));
                neuralNetwork.learn(epochSizeValue, digitsList.size()/10, digitsList, new NeuralNetwork.Callback() {
                    @Override
                    public void onLearnEpoch(int epoch, int totalEpochs,  int totalBatch, int correctAnswers) {
                        int learnPercent = Math.round(((float)epoch / totalEpochs) * 100f);
                        int epochSuccessPercent = Math.round(((float)correctAnswers / totalBatch) * 100f);
                        statusText.setText("Обучение сети " + learnPercent + "%");
                        graph.addScore(epochSuccessPercent);
                        if(graph.getScores().size() > 250){
                            List<Double> values = graph.getScores();
                            graph.setScores(new ArrayList<>());
                            for (int i = 0; i < 250; i++) {
                                if(i % 2 == 0){
                                    graph.addScore(values.get(i));
                                }
                            }
                        }
                        NeuralView.this.panel.invalidate();
                        // NeuralView.this.panel.repaint();
                    }

                    @Override
                    public void onFinished() {
                        neuralNetwork.save(new File(System.getProperty("user.dir"), "nn.train"));
                    }
                });
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()){
                    if(!digitsList.isEmpty()) {
                        Digits digit = digitsList.get(new Random().nextInt(digitsList.size()));
                        trainView.setIcon(new ImageIcon(ImageUtils.scale(digit.getImage(), 2)));
                        trainView.setText(String.valueOf(neuralNetwork.predict(digit)));
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
});
        startButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
               new Thread(new Runnable() {
                   @Override
                   public void run() {
                       epochSizeValue = Integer.parseInt(epochEdit.getText().toString());
                       generateCountValue = Integer.parseInt(generateCount.getText().toString());
                       digitsList = new ArrayList<>();
                       for (int i = 0; i < generateCountValue; i++) {
                           digitsList.add(new Digits(new Random().nextInt(100)));
                       }
                       neuralNetwork.learn(epochSizeValue, digitsList.size()/10, digitsList, new NeuralNetwork.Callback() {
                           @Override
                           public void onLearnEpoch(int epoch, int totalEpochs,  int totalBatch, int correctAnswers) {
                               int learnPercent = Math.round(((float)epoch / totalEpochs) * 100f);
                               int epochSuccessPercent = Math.round(((float)correctAnswers / totalBatch) * 100f);
                               statusText.setText("Обучение сети " + learnPercent + "%");
                               graph.addScore(epochSuccessPercent);
                               if(graph.getScores().size() > 250){
                                   List<Double> values = graph.getScores();
                                   graph.setScores(new ArrayList<>());
                                   for (int i = 0; i < 250; i++) {
                                       if(i % 2 == 0){
                                           graph.addScore(values.get(i));
                                       }
                                   }
                               }
                               NeuralView.this.panel.invalidate();
                              // NeuralView.this.panel.repaint();
                           }

                           @Override
                           public void onFinished() {
                               neuralNetwork.save(new File(System.getProperty("user.dir"), "nn.train"));
                           }
                       });
                   }
               }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (!Thread.currentThread().isInterrupted()){
                            if(!digitsList.isEmpty()) {
                                Digits digit = digitsList.get(new Random().nextInt(digitsList.size()));
                                trainView.setIcon(new ImageIcon(ImageUtils.scale(digit.getImage(), 4)));
                                trainView.setText(String.valueOf(neuralNetwork.predict(digit)));
                            }
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });
    }

    public static void main(String[] args) throws IOException {
        frame = new JFrame("Neural view");
        frame.setContentPane(new NeuralView().panel);
        frame.setSize(500, 400);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}

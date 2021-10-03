package com.liner.ragebot.neural;

public class NeuralNetworkData {
    private Layer[] layers;

    public NeuralNetworkData() {
    }

    public NeuralNetworkData(Layer[] layers) {
        this.layers = layers;
    }

    public void setLayers(Layer[] layers) {
        this.layers = layers;
    }

    public Layer[] getLayers() {
        return layers;
    }
}

package com.three.ars_2.simulation;

public class Brain {
    private Layer[] layers;

    public Brain(int[] numLayers){
        layers = new Layer[numLayers.length-1];
        for (int i = 0; i < layers.length; i++) {
            layers[i] = new Layer(numLayers[i], numLayers[i+1]);
        }
    }

    public double[] evaluate(double[] inputs){
        double[] outputs = new double[inputs.length];
        System.arraycopy(inputs, 0, outputs, 0, inputs.length);
        for (Layer layer : layers) {
            outputs = layer.evaluate(outputs);
        }
        return outputs;
    }

    private class Layer{
        private Neuron[] neurons;

        public Layer(int numInputs, int numNeurons){
            neurons = new Neuron[numNeurons];
            for (int i = 0; i < neurons.length; i++) {
                neurons[i] = new Neuron(numInputs);
            }
        }
        
        public double[] evaluate(double[] inputs){
            double[] outputs =  new double[neurons.length];

            for (int i = 0; i < neurons.length; i++) {
                outputs[i] = neurons[i].evaluate(inputs);
            }
            
            return outputs;
        }
        
        private class Neuron{
            private double bias;
            private double[] weights;

            public Neuron(int numInputs){
                this.bias = 2*Math.random()-1;
                this.weights = new double[numInputs];
                for (int i = 0; i < weights.length; i++) {
                    weights[i] = 2*Math.random()-1;
                }
            }

            public Neuron(double bias, double[] weights){
                this.bias = bias;
                this.weights = new double[weights.length];
                System.arraycopy(weights, 0, this.weights, 0, weights.length);
            }

            public double evaluate(double[] inputs){
                double preActivation = bias;
                for (int i = 0; i < inputs.length; i++) {
                    preActivation += weights[i] * inputs[i];
                }
                return sigmoid(preActivation);
            }

            private static double sigmoid(double input){
                return 1/(1+Math.exp(-input));
            }
        }
    }

}

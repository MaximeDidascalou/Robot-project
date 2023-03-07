package com.three.ars_2.simulation;

import java.util.Arrays;

public class NeuralNet {
    private final Layer[] LAYERS;
    private final boolean DO_RECURRENCE;

    //random nn constructor
    public NeuralNet(int[] structure, boolean doRecurrence){
        this.DO_RECURRENCE = doRecurrence;
        LAYERS = new Layer[structure.length-1];
        for (int i = 0; i < LAYERS.length; i++) {
            LAYERS[i] = new Layer(structure[i], structure[i+1]);
        }
    }
    
    //given genome nn constructor
    public NeuralNet(double[][][] nnGenome, boolean doRecurrence){
        this.DO_RECURRENCE = doRecurrence;
        LAYERS = new Layer[nnGenome.length];
        for (int i = 0; i < LAYERS.length; i++) {
            LAYERS[i] = new Layer(nnGenome[i]);
        }
    }

    public double[] evaluate(double[] inputs){
        double[] intermittentValues = copy(inputs);

        for (Layer layer : LAYERS) {
            intermittentValues = layer.evaluate(intermittentValues);
        }

        return intermittentValues;
    }

    private class Layer{
        private final Neuron[] NEURONS;
        private double[] memory;

        //random layer constructor
        public Layer(int numInputs, int numNeurons){
            NEURONS = new Neuron[numNeurons];
            for (int i = 0; i < NEURONS.length; i++) {
                if(DO_RECURRENCE){
                    numInputs += numNeurons;
                }
                NEURONS[i] = new Neuron(numInputs);
            }
            memory = new double[numNeurons];
            Arrays.fill(memory, 0);
        }
        
        //given genome layer constructor
        public Layer(double[][] layerGenome){
            NEURONS = new Neuron[layerGenome.length];
            for (int i = 0; i < NEURONS.length; i++) {
                NEURONS[i] = new Neuron(layerGenome[i]);
            }
            memory = new double[layerGenome.length];
            Arrays.fill(memory, 0);
        }
        
        public double[] evaluate(double[] inputs){
            double[] neuronInputs = DO_RECURRENCE ? concatenate(inputs, memory) : copy(inputs);

            for (int i = 0; i < NEURONS.length; i++) {
                memory[i] = NEURONS[i].evaluate(neuronInputs);
            }

            return copy(memory);
        }

        private class Neuron{
            private double bias;
            private double[] weights;

            //random neuron constructor
            public Neuron(int numInputs){
                this.bias = 2*Math.random()-1;
                this.weights = new double[numInputs];
                for (int i = 0; i < weights.length; i++) {
                    weights[i] = 2*Math.random()-1;
                }
            }

            //given genome constructor
            public Neuron(double[] neuronGenome){
                this.bias = neuronGenome[0];
                this.weights = new double[neuronGenome.length-1];
                System.arraycopy(neuronGenome, 1, weights, 0, weights.length);
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
            
            private double[] getGenome(){
                return concatenate(new double[]{bias}, weights);
            }
        }
        
        private double[][] getGenome(){
            double[][] layerGenome = new double[NEURONS.length][];
            for (int i = 0; i < NEURONS.length; i++) {
                layerGenome[i] = NEURONS[i].getGenome();
            }
            return layerGenome;
        }
    }

    public void mutate(double mutationRate){
        for (Layer layer : LAYERS) {
            for (Layer.Neuron neuron: layer.NEURONS) {
                if(Math.random()<mutationRate){
                    neuron.bias = 2*Math.random()-1;
                }
                for (int i = 0; i < neuron.weights.length; i++) {
                    if(Math.random()<mutationRate){
                        neuron.weights[i] = 2*Math.random()-1;
                    }
                }
            }
        }
    }

    public double[][][] getGenome(){
        double[][][] nnGenome = new double[LAYERS.length][][];
        for (int i = 0; i < LAYERS.length; i++) {
            nnGenome[i] = LAYERS[i].getGenome();
        }
        return nnGenome;
    }

    public boolean isRecurrent() {
        return DO_RECURRENCE;
    }

    private static double[] copy(double[] array) {
        double[] newArray = new double[array.length];
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }

    private static double[] concatenate(double[] first, double[] second){
        double[] newArray = new double[first.length + second.length];
        System.arraycopy(first, 0, newArray, 0, first.length);
        System.arraycopy(second, 0, newArray, first.length, second.length);
        return newArray;
    }
}

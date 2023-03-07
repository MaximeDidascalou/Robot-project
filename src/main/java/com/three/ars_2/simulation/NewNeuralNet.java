package com.three.ars_2.simulation;

public class NewNeuralNet {
    private double[][][] weights;
    private double[][] biases;
    private final boolean IS_RECURRENT;

    private double[][] activations;

    public NewNeuralNet(double[][][] weights, double[][] biases, boolean isRecurrent)
    {
        this.weights = weights;
        this.biases = biases;
        this.activations = new double[weights.length][];
        this.IS_RECURRENT = isRecurrent;
    }

    public NewNeuralNet(int[] structure, boolean isRecurrent){
        this.weights = new double[structure.length - 1][][];
        this.biases = new double[structure.length - 1][];
        this.activations = new double[structure.length][];
        this.IS_RECURRENT = isRecurrent;

        for (int i = 0; i < weights.length; i++) {
            weights[i] = new double[structure[i + 1]][];
            biases[i] = new double[structure[i + 1]];
            for (int j = 0; j < structure[i + 1]; j++) {
                weights[i][j] = new double[structure[i]];
                for (int k = 0; k < weights[i][j].length; k++) {
                    weights[i][j][k] = 2 * Math.random() - 1;
                }
                biases[i][j] = 2 * Math.random() - 1;
            }
        }
    }
    
    public double[] evaluate(double[] sensorInputs){
        activations[0] = copy(sensorInputs);

        for (int i = 1; i < this.activations.length; i++) {
            double[] inputs = IS_RECURRENT ? concatenate(activations[i - 1], activations[i]) : activations[i - 1];
            activations[i] = evaluateLayer(weights[i - 1], biases[i - 1], inputs);
        }
        
        return activations[activations.length-1];
    }
    
    public double[] evaluateLayer(double[][] weights, double[] biases, double[] inputs){
        double[] activations = new double[weights.length];

        for (int i = 0; i < weights.length; i++) {
            activations[i] = evaluateNeuron(weights[i], biases[i], inputs);
        }

        return activations;
    }
    
    private static double evaluateNeuron(double[] weights, double bias, double[] inputs){
        double activation = bias;

        for (int i = 0; i < inputs.length; i++) {
            activation += weights[i]*inputs[i];
        }
        
        return sigmoid(activation);
    }

    private static double sigmoid(double input){
        return 1/(1+Math.exp(-input));
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

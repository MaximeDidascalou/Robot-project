//////
////
//// Makes ANN for controlling robot movement
////
//// This file was written by Marco Rietjens
////
//////

package com.three.ars_2.simulation;

import java.util.Arrays;

public class ANN {
    private final boolean IS_RECURRENT;

    private double[][][] weights;
    private double[][] biases;

    private double[][] activations;

    enum CrossoverAlg {
        UNIFORM,
        INTERMEDIATE
    }

    // Creates the neural network based on given structure and wheter or not it should be recurrent. 
    //TODO make recurrence per layer setting -> boolean[]
    public ANN(int[] structure, boolean isRecurrent){
        this.IS_RECURRENT = isRecurrent;

        this.weights = new double[structure.length - 1][][];
        this.biases = new double[structure.length - 1][];
        this.activations = new double[structure.length][];

        for (int i = 0; i < weights.length; i++) {
            weights[i] = new double[structure[i + 1]][];
            biases[i] = new double[structure[i + 1]];
            for (int j = 0; j < structure[i + 1]; j++) {
                weights[i][j] = isRecurrent ? new double[structure[i] + structure[i + 1]] : new double[structure[i]];
                for (int k = 0; k < weights[i][j].length; k++) {
                    weights[i][j][k] = 2 * Math.random() - 1;
                }
                biases[i][j] = 2 * Math.random() - 1;
            }
        }

        for (int i = 0; i < activations.length; i++) {
            activations[i] = new double[structure[i]];
        }
    }


    // Creates new child neural net based on 2 parents and specified crossover algorithm
    public ANN(CrossoverAlg crossoverAlg, ANN firstANN, ANN secondANN){
        this.IS_RECURRENT = firstANN.IS_RECURRENT;

        this.weights = new double[firstANN.weights.length][][];
        this.biases = new double[firstANN.biases.length][];
        this.activations = new double[firstANN.activations.length][];

        for (int i = 0; i < firstANN.weights.length; i++) {
            weights[i] = new double[firstANN.weights[i].length][];
            biases[i] = new double[firstANN.biases[i].length];
            for (int j = 0; j < firstANN.weights[i].length; j++) {
                weights[i][j] = new double[firstANN.weights[i][j].length];
                for (int k = 0; k < firstANN.weights[i][j].length; k++) {
                    weights[i][j][k] = getCrossoverValue(crossoverAlg, firstANN.weights[i][j][k], secondANN.weights[i][j][k]);
                }
                biases[i][j] = getCrossoverValue(crossoverAlg, firstANN.biases[i][j], secondANN.biases[i][j]);
            }
        }

        for (int i = 0; i < activations.length; i++) {
            activations[i] = new double[firstANN.activations[i].length];
        }
    }

    // gets new value from 2 other values based on specified algorithm
    private double getCrossoverValue(CrossoverAlg crossoverAlg, double first, double second){
        switch (crossoverAlg) {
            case UNIFORM -> {
                return Math.random() < 0.5 ? first : second;
            }

            case INTERMEDIATE -> {
                double d = 0.25;
                double ratio = (1 + 2*d) * Math.random() - d;

                return ratio * first + (1 - ratio) * second;
            }

            default -> throw new IllegalStateException("Unexpected value: " + crossoverAlg);
        }
    }

    // Forward pass of neural net
    public double[] evaluate(double[] sensorInputs){
        activations[0] = copy(sensorInputs);

        for (int i = 1; i < this.activations.length; i++) {
            double[] inputs = IS_RECURRENT ? concatenate(activations[i - 1], activations[i]) : activations[i - 1];
            activations[i] = evaluateLayer(weights[i - 1], biases[i - 1], inputs);
        }
        
        return activations[activations.length-1];
    }
    
    // Forward pass of single layer
    public double[] evaluateLayer(double[][] weights, double[] biases, double[] inputs){
        double[] layerActivations = new double[weights.length];

        for (int i = 0; i < weights.length; i++) {
            layerActivations[i] = evaluateNeuron(weights[i], biases[i], inputs);
        }

        return layerActivations;
    }
    
    // get ouput of a neuron
    private static double evaluateNeuron(double[] weights, double bias, double[] inputs){
        double activation = bias;

        for (int i = 0; i < inputs.length; i++) {
            activation += weights[i]*inputs[i];
        }
        
        return sigmoid(activation);
    }

    // mutate (random) neural net based on mutation rate without taking into account initial value
    public void mutate(double mutationRate){
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                for (int k = 0; k < weights[i][j].length; k++) {
                    if(Math.random() < mutationRate){
                        weights[i][j][k] = 2 * Math.random() - 1;
                    }
                }
                if(Math.random() < mutationRate){
                    biases[i][j] = 2 * Math.random() - 1;
                }
            }
        }
    }

    // mutate (random) neural net based on mutation rate by changing values based on where they are now
    public void mutate_relative(double mutationRate){
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                for (int k = 0; k < weights[i][j].length; k++) {
                    if(Math.random() < mutationRate){
                        weights[i][j][k] += 1 * Math.random() - 0.5;
                    }
                }
                if(Math.random() < mutationRate){
                    biases[i][j] += 1 * Math.random() - 0.5;
                }
            }
        }
    }

    // sigmoid activation function
    private static double sigmoid(double input){
        return 1/(1+Math.exp(-input));
    }

    // return copy of array
    private static double[] copy(double[] array) {
        double[] newArray = new double[array.length];
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }

    // concatenate 2 arrays
    private static double[] concatenate(double[] first, double[] second){
        double[] newArray = new double[first.length + second.length];
        System.arraycopy(first, 0, newArray, 0, first.length);
        System.arraycopy(second, 0, newArray, first.length, second.length);
        return newArray;
    }
}

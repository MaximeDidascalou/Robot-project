package com.three.ars_2.simulation;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.*;

public class PSO_evolution {
    static int numIndividuals;
    static int numGenerations;
    static int numSurvivors;
    static double min_param;
    static double max_param;
    static double[][] parameters;
    static double[] performance;

    static int pso_particles;
    static int pso_iter;

    public static void main(String[] args){
        Random random = new Random();
        numIndividuals = 200;
        numGenerations = 200;
        numSurvivors = 20;
        min_param = 0.0;
        max_param = 3.0;

        pso_particles = 20;
        pso_iter = 200;

        performance = new double[numIndividuals];

        parameters = new double[numIndividuals][4];
        for (int i = 0; i < numIndividuals; i++) {
            for (int j = 0; j < 4; j++) {
                parameters[i][j] = random.uniform(min_param, max_param);
            }
        }

        doEvolution();

        for (int i = 0; i < parameters.length; i++) { //this equals to the row in our matrix.
            for (int j = 0; j < parameters[i].length; j++) { //this equals to the column in each row.
               System.out.print(parameters[i][j] + " ");
            }
            System.out.println(); //change line on console as row comes to end in the matrix.
         }

         for (int i = 0; i < parameters.length; i++) {
            System.out.println(performance[i]);
         }

        double[] test1 = {6,7,2,3};
        int[] test = getSortedIndices(test1);
        for (int i = 0; i < test.length; i++) {
            System.out.println(test[i]);
        }

        
    }

    static class Random extends java.util.Random{
        public double uniform(double from, double to){
            return (this.nextDouble()*(to-from))+from;
        }
    }
    

    public static void doEvolution(){
        for (int i = 0; i < numGenerations; i++) {
            PSO pso;
            for (int j = 0; j < numIndividuals; j++){
                pso = new PSO(pso_particles, pso_iter, parameters[j][0], parameters[j][1], parameters[j][2], parameters[j][3]);
                performance[j] = pso.runPOS();
            }

            int[] ranked_psos = getSortedIndices(performance);

            System.out.println("Generation: " + i + " | 0 performance: " + performance[ranked_psos[0]] + "last performance " + performance[ranked_psos[numIndividuals-1]]);
            int[] remaining_params = commitGenocide();
            doTheSexy(remaining_params);
        }
    }

    public static int[] commitGenocide(){
        int[] ranked_psos = getSortedIndices(performance);
        //return Arrays.copyOfRange(ranked_psos, performance.length-numSurvivors, performance.length);
        return Arrays.copyOfRange(ranked_psos, 0, numSurvivors);
    }

    public static void doTheSexy(int[] remaining_params){
        Random random = new Random();

        double[][] new_params = new double[numIndividuals][4];

        // keeping selected parameters
        for (int i = 0; i < numSurvivors; i++) {
            for (int j = 0; j < 4; j++){
                new_params[i][j] = parameters[remaining_params[i]][j];
            }
        }
        // constructing new parameters
        for (int i = numSurvivors; i < numIndividuals; i++) {
            int ind_1 = ThreadLocalRandom.current().nextInt(0, numSurvivors);
            int ind_2 = ThreadLocalRandom.current().nextInt(0, numSurvivors);
            for (int j = 0; j < 4; j++){
                double choice = Math.random();
                if (choice < 0.1){
                    new_params[i][j] = (new_params[ind_1][j] + new_params[ind_2][j])/2;
                } else if (choice < 0.54) {
                    new_params[i][j] = new_params[ind_1][j];
                } else if (choice < 0.98) {
                    new_params[i][j] = new_params[ind_2][j];
                } else {
                    new_params[i][j] = random.uniform(min_param, max_param);
                }

            }
        }

        parameters = new_params;
    }



    public static int[] getSortedIndices(double[] originalArray){
        int len = originalArray.length;

        double[] sortedCopy = originalArray.clone();
        int[] indices = new int[len];

        // Sort the copy
        Arrays.sort(sortedCopy);

        // Go through the original array: for the same index, fill the position where the
        // corresponding number is in the sorted array in the indices array
        for (int index = 0; index < len; index++)
            indices[index] = Arrays.binarySearch(sortedCopy, originalArray[index]);

        return indices;
    }
}

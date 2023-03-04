package com.three.ars_2.simulation;

import java.util.*;

public class Population {
    private final World WORLD;
    private int populationSize;
    private int individualCount;
    private List<Robot> robots = new ArrayList<>();
    public Population(World world){
        this.WORLD = world;
    }

    public void populate(int populationSize) {
        this.populationSize = populationSize;
        for (int i = 0; i < populationSize; i++) {
            robots.add(new Robot(WORLD,  String.valueOf(i)));
        }
        individualCount = populationSize;
    }

    public void sortIndividuals(){
        Collections.sort(robots);
    }

    public void commitGenocide(int numSurvivors){
        sortIndividuals();
        robots.subList(0, robots.size()-numSurvivors).clear();
    }

    public void commitGenocide(double ratioSurvivors){
        sortIndividuals();
        robots.subList(0, (int)(robots.size()*(1.0-ratioSurvivors))).clear();
    }

    public void doTheSexy(){
        Random random = new Random();
        List<Robot> babies = new ArrayList<>();
        for (int i = 0; i < populationSize-robots.size(); i++) {
            babies.add(getOffspring(robots.get(random.nextInt(robots.size())), robots.get(random.nextInt(robots.size()))));
        }
        robots.addAll(babies);
    }

    public Robot getOffspring(Robot first, Robot second){
        NeuralNet newNeuralNet = new NeuralNet(randomMergeGenomes(first.getNeuralNet().getGenome(), second.getNeuralNet().getGenome()), first.getNeuralNet().isRecurrent());
        Robot baby = new Robot(WORLD, individualCount++ + ":(" + first.getName() + "+" + second.getName() + ")", newNeuralNet);
        baby.getNeuralNet().mutate(1.0/4);
        return baby;
    }

    private double[][][] randomMergeGenomes(double[][][] firstGenome, double[][][] secondGenome){
        double[][][] newGenome = new double[firstGenome.length][][];
        for (int i = 0; i < firstGenome.length; i++) {
            newGenome[i] = new double[firstGenome[i].length][];
            for (int j = 0; j < firstGenome[i].length; j++) {
                newGenome[i][j] = new double[firstGenome[i][j].length];
                for (int k = 0; k < firstGenome[i][j].length; k++) {
                    newGenome[i][j][k] = Math.random() < 0.5 ? firstGenome[i][j][k] : secondGenome[i][j][k];
                }
            }
        }

        return newGenome;
    }

    public List<Robot> getRobots() {
        return robots;
    }
}

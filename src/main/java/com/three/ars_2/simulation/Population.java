package com.three.ars_2.simulation;

import java.util.*;

public class Population {
    private final World WORLD;
    private int individualCount;
    private List<Robot> individuals = new ArrayList<>();
    private Robot[] selectedIndividuals;

    enum SelectionAlg {
        ROULETTE,
        TOURNAMENT,
        TRUNCATION
    }

    public Population(World world){
        this.WORLD = world;
        this.individualCount = WORLD.getPopulationSize();
        for (int i = 0; i < WORLD.getPopulationSize(); i++) {
            individuals.add(new Robot(WORLD, String.valueOf(i)));
        }
    }

    public void sortIndividuals(){
        Collections.sort(individuals);
    }

    public void doSelection(SelectionAlg selectionAlg, int selectionCount, int elitismCount){
        sortIndividuals();
        selectedIndividuals = new Robot[selectionCount];

        elitismCount = Math.min(selectionCount, elitismCount);
        for (int i = 0; i < elitismCount; i++) {
            selectedIndividuals[i] = individuals.get(i);
        }

        switch (selectionAlg){
            case ROULETTE -> {

            }
            case TOURNAMENT -> {

            }
            case TRUNCATION -> {
                for (int i = elitismCount; i < selectionCount; i++) {
                    selectedIndividuals[i] = individuals.get(i);
                }
            }
        }
    }

    public void doCrossover(NewNeuralNet.CrossoverAlg crossoverAlg, double mutationRate){

        Robot newRobot = new Robot(WORLD, String.valueOf(individualCount++), new NeuralNet(firstRobot.getNeuralNet(), secondRobot.getNeuralNet()))

                double[][][] firstGenome = firstRobot. secondGenome;
                double[][][] newGenome = new double[firstGenome.length][][];

                for (int i = 0; i < .length; i++) {
                    newGenome[i] = new double[firstGenome[i].length][];
                    for (int j = 0; j < firstGenome[i].length; j++) {
                        newGenome[i][j] = new double[firstGenome[i][j].length];
                        for (int k = 0; k < firstGenome[i][j].length; k++) {
                            newGenome[i][j][k] = Math.random() < 0.5 ? firstGenome[i][j][k] : secondGenome[i][j][k];
                        }
                    }
                }

                return new Robot(WORLD, String.valueOf(individualCount++)), new newGenome;


        }

        individuals.addAll(newGeneration);
    }

    public void resetIndividuals(){
        for(Robot robot: individuals){
            robot.reset();
        }
    }

    public List<Robot> getIndividuals() {
        return individuals;
    }
}

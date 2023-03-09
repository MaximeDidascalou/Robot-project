package com.three.ars_2.simulation;

import java.util.*;

public class Population {
    private final World WORLD;
    private int individualCount;
    private Robot[] individuals;


    enum SelectionAlg {
        RANK,
        ROULETTE,
        TOURNAMENT
    }

    public Population(World world){
        this.WORLD = world;
        this.individualCount = WORLD.getPopulationSize();
        this.individuals = new Robot[WORLD.getPopulationSize()];
        for (int i = 0; i < WORLD.getPopulationSize(); i++) {
            individuals[i] = new Robot(WORLD, String.valueOf(i));
        }
    }

    public void sortIndividuals(){
        Arrays.sort(individuals);
    }

    public void doNewGeneration(SelectionAlg selectionAlg, ANN.CrossoverAlg crossoverAlg, int elitismCount) {
        Robot[] newIndividuals = new Robot[WORLD.getPopulationSize()];
        sortIndividuals();

        elitismCount = Math.min(WORLD.getPopulationSize(), elitismCount);
        if (elitismCount > 0) {
            System.arraycopy(individuals, 0, newIndividuals, 0, elitismCount);
        }

        for (int i = elitismCount; i < WORLD.getPopulationSize(); i++) {
            Robot firstIndividual = selectIndividual(selectionAlg);
            Robot secondIndividual = selectIndividual(selectionAlg);

            ANN newANN = new ANN(crossoverAlg, firstIndividual.getANN(), secondIndividual.getANN());
            newANN.mutate(0.02);

            newIndividuals[i] =  new Robot(WORLD, String.valueOf(individualCount++), newANN);
        }

        individuals = newIndividuals;
        resetIndividuals();
    }

    private Robot selectIndividual(SelectionAlg selectionAlg){
        switch (selectionAlg){
            case RANK -> {
                return individuals[0];
            }
            case ROULETTE -> {
                return individuals[0];
            }
            case TOURNAMENT -> {
                Robot bestRobot = individuals[(int)(Math.random()*individuals.length)];
                for (int i = 1; i < 10; i++) {
                     Robot newRobot = individuals[(int) (Math.random()*individuals.length)];
                    if(newRobot.getFitness() > bestRobot.getFitness())
                        bestRobot = newRobot;
                }
                return bestRobot;
            }
            default -> throw new IllegalStateException("Unexpected value: " + selectionAlg);
        }
    }

    public void doGenocide(int numIndividuals){
        sortIndividuals();
        Robot[] newIndividuals = new Robot[numIndividuals];
        System.arraycopy(individuals, 0, newIndividuals, 0, numIndividuals);
        individuals = newIndividuals;
        resetIndividuals();
    }

    public void resetIndividuals(){
        for(Robot robot: individuals){
            robot.reset();
        }
    }

    public Robot[] getIndividuals() {
        return individuals;
    }
}

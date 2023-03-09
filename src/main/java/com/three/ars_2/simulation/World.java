package com.three.ars_2.simulation;

import com.three.ars_2.gui.GuiSettings;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;

public class World {
    private final double HEIGHT;
    private final double WIDTH;
    private double[] START_POSITION;
    private double START_ANGLE;
    private final double[][] ENVIRONMENT;
    private final double DUST_RESOLUTION;


    private double timeStep;
    private final int POPULATION_SIZE;
    private final int NUMBER_GENERATIONS;
    private final int RUNS_PER_INDIVIDUAL;
    private final int SIMULATION_SECONDS;
    private Population population;

    public World() {
        this.HEIGHT = 6;
        this.WIDTH = 3;
        this.START_POSITION = new double[]{0.5,0.5};
        this.START_ANGLE = 0.0;
        this.ENVIRONMENT = createEnvironment();
        this.DUST_RESOLUTION = 1.0/8;

        this.timeStep = 1.0/4;
        this.POPULATION_SIZE = 100;
        this.NUMBER_GENERATIONS = 1000;
        this.RUNS_PER_INDIVIDUAL = 10;
        this.SIMULATION_SECONDS = 60;
        this.population = new Population(this);
    }

    private double[][] createEnvironment() {
        List<double[]> environment = new ArrayList<>();

        environment.add(new double[] {0, HEIGHT, WIDTH, HEIGHT});
        environment.add(new double[] {0, 0, WIDTH, 0});
        environment.add(new double[] {0, 0, 0, HEIGHT});
        environment.add(new double[] {WIDTH, 0, WIDTH, HEIGHT});


        environment.add(new double[] {0, 2, 1, 2});
        environment.add(new double[] {2, 2, 3, 2});
        environment.add(new double[] {1, 4, 2, 4});

//        environment.add(new double[] {1, 1, 1, 3});
//        environment.add(new double[] {1, 2, 3, 2});
//        environment.add(new double[] {3, 1, 3, 3});
//        environment.add(new double[] {2, 0, 2, 1});
//        environment.add(new double[] {2, 3, 2, 4});

//        environment.add(new double[] {1, 1, 5, 1});
//        environment.add(new double[] {2, 1, 2, 5});
//        environment.add(new double[] {0, 2, 1, 2});
//        environment.add(new double[] {1, 3, 2, 3});
//        environment.add(new double[] {0, 4, 1, 4});
//        environment.add(new double[] {1, 5, 5, 5});
//        environment.add(new double[] {2, 3, 5, 1});
//        environment.add(new double[] {2, 3, 5, 5});
//        environment.add(new double[] {4, 3, 7, 1});
//        environment.add(new double[] {4, 3, 7, 5});

        return environment.toArray(new double[0][]);
    }

    public void runEvolution(){
        for (int i = 0; i < NUMBER_GENERATIONS; i++) {
            runGeneration();

            System.out.println("Generation: " + i + " | Best fitness: " + population.getIndividuals()[0].getFitness());

            population.doNewGeneration(Population.SelectionAlg.TOURNAMENT, ANN.CrossoverAlg.INTERMEDIATE, (int)(POPULATION_SIZE * 0.1));
        }
        runGeneration();
        population.doGenocide(1);
    }

    //TODO add number of trials and add support in ROBOT
    public void runGeneration() {
        for (int i = 0; i < SIMULATION_SECONDS/ timeStep; i++) {
            updateRobots();
        }

        for (Robot robot : population.getIndividuals()){
            robot.calculateFitness();
        }
    }

    public void updateRobots(){
        for(Robot robot: population.getIndividuals()){
            robot.update();
        }
    }

    public void draw(GraphicsContext g){
        g.setFill(GuiSettings.BACKGROUND_COLOR);
        g.fillRect(0,0, getWidth()*GuiSettings.SCALING, getHeight()*GuiSettings.SCALING);
        g.setFill(GuiSettings.WALL_COLOR);
        double[][] environment = getEnvironment();
        for(double[] wall : environment){
            g.setStroke(GuiSettings.WALL_COLOR);
            g.setLineWidth(2.0);
            g.strokeLine(wall[0]*GuiSettings.SCALING, wall[1]*GuiSettings.SCALING, wall[2]*GuiSettings.SCALING, wall[3]*GuiSettings.SCALING);
        }
    }

    public double getHeight() {
        return HEIGHT;
    }
    public double getWidth() {
        return WIDTH;
    }
    public double[] getStartPosition() {
        return START_POSITION;
    }
    public double getStartAngle() {
        return START_ANGLE;
    }
    public double[][] getEnvironment(){ return ENVIRONMENT; }
    public double getDustResolution() {
        return DUST_RESOLUTION;
    }
    public double getTimeStep(){
        return timeStep;
    }
    public void setTimeStep(double timeStep){
        this.timeStep = timeStep;
    }
    public int getPopulationSize() {
        return POPULATION_SIZE;
    }
    public Population getPopulation() {
        return population;
    }
}

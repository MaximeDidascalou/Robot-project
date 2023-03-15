package com.three.ars_2.simulation;

import com.three.ars_2.gui.GuiSettings;
import javafx.scene.canvas.GraphicsContext;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class World {
    private final double HEIGHT = 6;
    private final double WIDTH = 6;
    private double[] START_POSITION = new double[]{0.5,0.5};
    private double START_ANGLE = 0.0;
    private final double[][] ENVIRONMENT = createEnvironment();
    private final double[][] LANDMARKS = createLandmarks();
    private final double DUST_RESOLUTION = 1.0/8;

    private static final int NUMBER_ROBOTS = 1;
    private final int NUMBER_GENERATIONS = 64;
    private final int NUMBER_TRIALS = 8;
    private final int SIMULATION_SECONDS = 60;

    private final boolean CROSSOVER = true;
    private final boolean RANDOM_START_POSITION = true;

    enum SelectionAlg {
        RANK,
        PROPORTIONATE,
        TOURNAMENT
    }


    private double timeStep;
    private Robot[] robots;
    private int numRobotsInHistory;

    public World() {
        this.robots = new Robot[0];
    }

    private double[][] createEnvironment() {
        List<double[]> environment = new ArrayList<>();

        environment.add(new double[] {0, HEIGHT, WIDTH, HEIGHT});
        environment.add(new double[] {0, 0, WIDTH, 0});
        environment.add(new double[] {0, 0, 0, HEIGHT});
        environment.add(new double[] {WIDTH, 0, WIDTH, HEIGHT});


        environment.add(new double[] {2, 2, 2, 4});
        environment.add(new double[] {2, 4, 4, 4});
        environment.add(new double[] {4, 4, 4, 2});
        environment.add(new double[] {4, 2, 2, 2});

//        environment.add(new double[] {1, 1, 1, 3});
//        environment.add(new double[] {1, 2, 3, 2});
//        environment.add(new double[] {3, 1, 3, 3});
//        environment.add(new double[] {2, 0, 2, 1});
//        environment.add(new double[] {2, 3, 2, 4});

        return environment.toArray(new double[0][]);
    }

    private double[][] createLandmarks(){
        List<double[]> landmarks = new ArrayList<>(); // {x, y, signature}
        //landmarks.add(new double[] {ENVIRONMENT[0][0], ENVIRONMENT[0][1], 1});
        //landmarks.add(new double[] {ENVIRONMENT[0][2], ENVIRONMENT[0][3], 2});
        double signature = 1;
        for (int i = 0; i < ENVIRONMENT.length; i++){
            double[] wall = ENVIRONMENT[i];
            for (int j = 0; j < 3; j+=2){
                boolean addToLandmarks = true;
                for (int k =0; k < landmarks.size(); k++){
                    if ((wall[j] == landmarks.get(k)[0] && wall[j+1] == landmarks.get(k)[1]) ||
                        (wall[j] > WIDTH || wall[j] < 0 || wall[j+1] > HEIGHT || wall[j+1] < 0)) {
                        addToLandmarks = false;
                        break;
                    }
                }
                if (addToLandmarks){
                    landmarks.add(new double[] {wall[j], wall[j+1], signature});
                    signature++;
                }
            }
        }
        return landmarks.toArray(new double[0][]);
    }

    public void runEvolution(Robot[] seedRobots) {
        DecimalFormat df = new DecimalFormat();
        timeStep = 1.0/4;

        this.robots = seedRobots;

        runSimulation();
        double averageFitness = 0;
        for (Robot robot: robots){
            averageFitness += robot.getFitness();
        }
        averageFitness = averageFitness/robots.length;

        df.setMaximumFractionDigits(3);
        System.out.println("[ Starting Generation | Average fitness: " + averageFitness +
                " | Best robot: " + robots[0].getName() +
                " | Fitness: " + df.format( robots[0].getFitness()) + " ]");

        for (int i = 0; i < NUMBER_GENERATIONS; i++) {
            createNewGeneration(SelectionAlg.TOURNAMENT, ANN.CrossoverAlg.INTERMEDIATE, NUMBER_ROBOTS,  (int)(NUMBER_ROBOTS * 0.05));
            runSimulation();
            averageFitness = 0;
            for (Robot robot: robots){
                averageFitness += robot.getFitness();
            }
            averageFitness = averageFitness/robots.length;

            df.setMaximumFractionDigits(3);
            System.out.println("[ Generation: " + (i+1) +" | Average fitness: " + averageFitness +
                    " | Best robot: " + robots[0].getName() +
                    " | Fitness: " + df.format( robots[0].getFitness()) + " ]");
        }
    }

    public void initialiseRobots(){
        robots = new Robot[NUMBER_ROBOTS];
        for (int i = 0; i < robots.length; i++) {
            robots[i] = new Robot(this, String.valueOf(i), new ANN(new int[]{12, 20, 2}, true));
            if (RANDOM_START_POSITION){
                robots[i].initialise();
            } else {
                robots[i].initialise(START_POSITION, START_ANGLE);
            }
        }
        numRobotsInHistory = NUMBER_ROBOTS;
    }

    public void runSimulation() {
        for (Robot robot: robots){
            double fitnessSum = 0;
            for (int trial = 0; trial < NUMBER_TRIALS; trial++) {
                if (RANDOM_START_POSITION){
                    robot.initialise();
                } else {
                    robot.initialise(new double[]{WIDTH/2,HEIGHT/2}, 0.0);
                }

                for (int tick = 0; tick < SIMULATION_SECONDS/ timeStep; tick++) {
                    robot.update();
                }
                robot.calculateFitness();
                fitnessSum += robot.getFitness();
            }
            robot.setFitness(fitnessSum / NUMBER_TRIALS);
        }
        Arrays.sort(robots);
    }

    public void createNewGeneration(SelectionAlg selectionAlg, ANN.CrossoverAlg crossoverAlg, int generationSize, int elitismCount) {
        Robot[] newGeneration = new Robot[generationSize];

        elitismCount = Math.min(generationSize, elitismCount);
        if (elitismCount > 0) {
            System.arraycopy(robots, 0, newGeneration, 0, elitismCount);
        }

        if (CROSSOVER){
            for (int i = elitismCount; i < generationSize; i++)
            {
                Robot firstRobot = selectRobot(selectionAlg);
                Robot secondRobot = selectRobot(selectionAlg);
                ANN newANN = new ANN(crossoverAlg, firstRobot.getANN(), secondRobot.getANN());
                newANN.mutate(0.05);

                newGeneration[i] =  new Robot(this, String.valueOf(numRobotsInHistory++), newANN);
            }
        } else {
            for (int i = elitismCount; i < generationSize; i++)
            {
                Robot selected_robot = selectRobot(selectionAlg);
                ANN newANN = selected_robot.getANN();
                newANN.mutate((i-elitismCount)/(1.0*generationSize-elitismCount)); // _relative
                newGeneration[i] =  new Robot(this, String.valueOf(numRobotsInHistory++), newANN);
            }

        }
        robots = newGeneration;
    }

    private Robot selectRobot(SelectionAlg selectionAlg){
        switch (selectionAlg){
            case RANK -> {
                double summedRank = (robots.length * (robots.length + 1))/2.0;
                double random = Math.random();
                double cumSumRank = 0;
                for (int i = 0; i<robots.length;i++){
                    cumSumRank += 1 - (i+1)/ summedRank;
                    if (random < cumSumRank){
                        return robots[i];
                    }
                }

                return robots[0];
            }
            case PROPORTIONATE -> {
                double summedFitness = 0;
                for (Robot robot : robots) {
                    summedFitness += robot.getFitness();
                }
                double random = Math.random();
                double cumSumFitness = 0;
                for (Robot robot : robots) {
                    cumSumFitness += robot.getFitness() / summedFitness;
                    if (random < cumSumFitness) {
                        return robot;
                    }
                }

                return robots[0];
            }
            case TOURNAMENT -> {
                Robot bestRobot = robots[(int)(Math.random()* robots.length)];
                for (int i = 1; i < 8; i++) {
                    Robot newRobot = robots[(int) (Math.random()* robots.length)];
                    if(newRobot.getFitness() > bestRobot.getFitness())
                        bestRobot = newRobot;
                }
                return bestRobot;
            }
            default -> throw new IllegalStateException("Unexpected value: " + selectionAlg);
        }
    }

    public void updateRobots() {
        for (Robot robot : robots) {
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
    public double[][] getEnvironment(){
        return ENVIRONMENT;
    }
    public double[][] getLandmarks(){
        return LANDMARKS;
    }
    public double getDustResolution() {
        return DUST_RESOLUTION;
    }
    public double getTimeStep(){
        return timeStep;
    }
    public void setTimeStep(double timeStep){
        this.timeStep = timeStep;
    }
    public Robot[] getRobots() {
        return robots;
    }
}

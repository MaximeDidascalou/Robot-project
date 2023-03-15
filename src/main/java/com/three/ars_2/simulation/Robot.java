package com.three.ars_2.simulation;

import com.three.ars_2.gui.GuiSettings;
import com.three.ars_2.gui.Vector2D;
import javafx.scene.canvas.GraphicsContext;

import java.lang.Math;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class Robot implements Comparable<Robot>{
    private static final int NUM_SENSORS = 12;; //number of sensors
    private static final double MAX_SENSOR_DISTANCE = 1.5; //Maximum distance a sensor can see
    private static final double[] SENSOR_ANGLES = new double[NUM_SENSORS]; //Sensor angles, relative to bot angle
    private static final double DIAMETER = 0.5; //diameter of the robot
    private static final double WHEEL_DISTANCE = 0.4; //length between wheels
    private static final double MAX_WHEEL_SPEED = 2.0; //maximum speed of the wheels
    private static final boolean DO_ACCELERATION = true;
    private static final double MAX_ACCELERATION = 4.0;
    private static final double[] FITNESS_WEIGHTS = new double[]{1.0, 0.0,-1.0}; //weights for weighted fitness sum [dust weight, wall weight, turning weight]

    private final World WORLD; //reference to the world it is in
    private final String NAME;
    private final ANN ANN;

    private double[] position = new double[2];
    private double angle;
    private double[] wheelSpeeds = new double[2];
    private double[] sensorValues = new double[NUM_SENSORS];;
    private boolean[][] dustIsCollected;
    private int totalDustCollected, ticksInWall, totalTicks;
    private double summedTurningPenalty, fitness;


    // KALMAN FILTER PARAMETERS;

    private static final boolean DRAWKALMAN = true;
    private double velocity;
    private double omega;
    private double[][] state_guess = new double[3][1];
    private double[][] state_true = new double[3][1];
    private double[][] covariance = new double[][]{{0.1,0,0},
                                                   {0,0.1,0},
                                                   {0,0,0.1}};
    private double[][] R = new double[][]{{0.00001,0,0},
                                          {0,0.00001,0},
                                          {0,0,0.00001}};
    private double[][] Q = new double[][]{{0.5,0,0},
                                          {0,0.5,0},
                                          {0,0,0.5}};
    private double[][] landmarks;

    static {
        if(NUM_SENSORS > 1 && SENSOR_ANGLES[1] == 0){
            double angleInterval = 2*Math.PI/ NUM_SENSORS;

            for(int i = 0; i < NUM_SENSORS; i++) {
                SENSOR_ANGLES[i] = i*angleInterval;
            }
        }
    }

    Robot(World world, String name, ANN ann){
        this.WORLD = world;
        this.NAME = name;
        this.ANN = ann;
    }

    public void initialise(){
//        initialise(WORLD.getStartPosition(), WORLD.getStartAngle());
        initialise(new double[]{Math.random()*WORLD.getWidth(),Math.random()* WORLD.getHeight()}, Math.random()*Math.PI*2);
    }

    public void initialise(double[] position, double angle){
        this.position[0] = position[0];
        this.position[1] = position[1];
        this.angle = angle;
        updateSensorValues();
        this.wheelSpeeds = new double[]{0, 0};
        this.dustIsCollected = new boolean[(int)(WORLD.getWidth()/WORLD.getDustResolution())][(int)(WORLD.getHeight()/WORLD.getDustResolution())];
        this.totalDustCollected = 0;
        this.ticksInWall = 0;
        this.summedTurningPenalty = 0;
        this.totalTicks = 0;
        this.fitness = Double.MIN_VALUE;

        // KALMAN
        
        state_guess = new double[][]{{position[0]},{position[1]},{angle}};
        state_true = new double[][]{{position[0]},{position[1]},{angle}};
        velocity = 0.3;
        omega = 0.2;

    }

    public void update(){
        updateWheelSpeeds();
        updatePosition();
        updateDustCollection();
        updateSensorValues();
        totalTicks++;
    }

    // update sensor values
    private void updateSensorValues() {
        for (int i = 0; i < NUM_SENSORS; i++) {
            sensorValues[i] = calculateSensorValue(angle + SENSOR_ANGLES[i]);
        }
    }


    private double calculateSensorValue(double sensorAngle) {
        double sensorX = position[0] + (MAX_SENSOR_DISTANCE + DIAMETER/2) * Math.cos(sensorAngle);
        double sensorY = position[1] + (MAX_SENSOR_DISTANCE + DIAMETER/2) * Math.sin(sensorAngle);
        double minimumSquared = Math.pow(MAX_SENSOR_DISTANCE + DIAMETER/2, 2);
        for (double[] wall : WORLD.getEnvironment()) {
            double[] intersect = lineIntersect(position[0], position[1], sensorX, sensorY, wall[0], wall[1], wall[2], wall[3]);
            if (intersect != null) {
                double distanceSquared = Math.pow((intersect[1] - position[1]), 2) + Math.pow((intersect[0] - position[0]), 2);
                if (distanceSquared < minimumSquared) {
                    minimumSquared = distanceSquared;
                }
            }
        }
        return Math.sqrt(minimumSquared) - DIAMETER/2;
    }

    public void updateWheelSpeeds(){
        double[] adjustedSensorValues = new double[sensorValues.length];
        Arrays.setAll(adjustedSensorValues, i -> Math.exp(-5 * sensorValues[i] / MAX_SENSOR_DISTANCE));
        double[] newWheelSpeeds = ANN.evaluate(adjustedSensorValues);
        setWheelSpeeds(newWheelSpeeds[0] * MAX_WHEEL_SPEED, newWheelSpeeds[1] * MAX_WHEEL_SPEED);

        summedTurningPenalty += -Math.pow(Math.abs(wheelSpeeds[0] - wheelSpeeds[1])-1, 2)+1;
    }

    

    public void updatePosition(){
        double[] newPosition = new double[2];

        if (Math.abs(wheelSpeeds[0] - wheelSpeeds[1]) < 0.000001) {
            newPosition[0] = position[0] + Math.cos(angle) * wheelSpeeds[0] * WORLD.getTimeStep();
            newPosition[1] = position[1] + Math.sin(angle) * wheelSpeeds[0] * WORLD.getTimeStep();
        } else {
            double radius = (WHEEL_DISTANCE / 2) * ((wheelSpeeds[0] + wheelSpeeds[1]) / (wheelSpeeds[1] - wheelSpeeds[0]));
            double omega_t = (wheelSpeeds[1] - wheelSpeeds[0]) / WHEEL_DISTANCE;

            double[] ICC = {position[0] - radius * Math.sin(angle), position[1] + radius * Math.cos(angle)};
            newPosition[0] = Math.cos(omega_t*WORLD.getTimeStep())*(position[0] - ICC[0]) - Math.sin(omega_t*WORLD.getTimeStep())*(position[1] - ICC[1]) + ICC[0];
            newPosition[1] = Math.sin(omega_t*WORLD.getTimeStep())*(position[0] - ICC[0]) + Math.cos(omega_t*WORLD.getTimeStep())*(position[1] - ICC[1]) + ICC[1];

            angle = angle + omega_t * WORLD.getTimeStep();
        }

        collisionCheck(newPosition);
        position = newPosition;
    }

    public void updateKalman(){
        System.out.println(velocity + " " + omega);
        // prediction:
        double[][] action = new double[][]{{velocity}, 
                                           {omega}};

        double[][] B = new double[][]{{WORLD.getTimeStep() * Math.cos(angle), 0},
                                      {WORLD.getTimeStep() * Math.sin(angle), 0},
                                      {0, WORLD.getTimeStep()}};

        state_true = addMatrix(state_true, multiplyMatrix(B,action),false); // + noise
        java.util.Random r = new java.util.Random();
        for (int i = 0; i<3; i++){
            state_true[i][0] += (r.nextGaussian() * Math.sqrt(R[i][i]));
        }
        angle = state_true[2][0];

        double[] newPosition = new double[] {state_true[0][0], state_true[1][0]};
        collisionCheck(newPosition);
        position = newPosition;
        state_true[0][0] = position[0];
        state_true[1][0] = position[1];
        
        state_guess = addMatrix(state_guess, multiplyMatrix(B,action),false);
        covariance = addMatrix(covariance, R,false);
        
        // correction:
        double[][] I = new double[][]{{1,0,0},
                                      {0,1,0},
                                      {0,0,1}};
        double[][] z = getZ();
        if (z != null) {
            double[][] K = multiplyMatrix(covariance, inverseDiag(addMatrix(covariance, Q,false)));
            state_guess = addMatrix(state_guess, multiplyMatrix(K, addMatrix(z, state_guess,true)),false);
            covariance = multiplyMatrix(addMatrix(I, K, true), covariance);
            
        }
        
    }

    private double[][] getZ(){
        List<double[]> landmarksInRange = new ArrayList<>();
        double[][] observed_state = new double[3][1];
        for (double[] landmark : WORLD.getLandmarks()) {
            double r = Math.sqrt(Math.pow(position[0] - landmark[0], 2) + Math.pow(position[1] - landmark[1], 2));
            //double bearing = Math.atan2(position[1] - landmark[1], position[0] - landmark[0]) - angle;
            if (r<MAX_SENSOR_DISTANCE) {
                landmarksInRange.add(new double[] {landmark[0], landmark[1]});
            }
        }
        landmarks = landmarksInRange.toArray(new double[0][]);
        if (landmarksInRange.size() > 1){
            java.util.Random r = new java.util.Random();
            for (int i = 0; i < 3; i++){
                observed_state[i][0] = state_true[i][0] + (r.nextGaussian() * Math.sqrt(Q[i][i]));
            }
        } else {
            return null;
        }
        return observed_state;
    }

    private double[][] inverseDiag(double X[][]){
        int i;
        int row = X.length;
        int col = X[0].length;
        if (row != col) {
            System.out.println(
                "\nMatrix not square");
            return null;
        }
        double[][] inverse = Arrays.stream(X).map(double[]::clone).toArray(double[][]::new);
        for (i=0; i<row; i++){
            inverse[i][i] = 1/inverse[i][i];
        }
        return inverse;
    }

    private double[][] multiplyMatrix(double M1[][], double M2[][]){
        int i, j, k;

        int row1 = M1.length;
        int col1 = M1[0].length;

        int row2 = M2.length;
        int col2 = M2[0].length;

        if (row2 != col1) {
            System.out.println(
                "\nMultiplication Not Possible");
            return null;
        }
        double product[][] = new double[row1][col2];
        for (i = 0; i < row1; i++) {
            for (j = 0; j < col2; j++) {
                for (k = 0; k < row2; k++)
                    product[i][j] += M1[i][k] * M2[k][j];
            }
        }
 
        return product;
    }

    private double[][] addMatrix(double[][] M1, double[][] M2, boolean subtract){
        int i, j;

        int row1 = M1.length;
        int col1 = M1[0].length;

        int row2 = M2.length;
        int col2 = M2[0].length;

        if (row1 != row2 || col1 != col2) {
            System.out.println(
                "\nAddition Not Possible");
            return null;
        }
        double sum[][] = new double[row1][col1];
        for (i = 0; i < row1; i++) {
            for (j = 0; j < col1; j++) {
                sum[i][j] = M1[i][j] + (M2[i][j] * (subtract ? -1 : 1));
            }
        }
        return sum;
    }

    private void updateDustCollection(){
        for(int i = Math.max((int)((position[0] - DIAMETER/2)/ WORLD.getDustResolution()), 0); i < Math.min((int)((position[0] + DIAMETER/2)/ WORLD.getDustResolution()) + 2, dustIsCollected.length); i++) {
            for (int j = Math.max((int)((position[1] - DIAMETER/2)/ WORLD.getDustResolution()),0); j < Math.min((int)((position[1] + DIAMETER/2)/ WORLD.getDustResolution()) + 2, dustIsCollected[i].length); j++) {
                if(!dustIsCollected[i][j] && dustDistanceSquared(i, j) < Math.pow(DIAMETER/2,2)){
                    dustIsCollected[i][j] = true;
                    totalDustCollected++;
                }
            }
        }
    }

    private double dustDistanceSquared(int i, int j) {
        double dustX = (i + 0.5)* WORLD.getDustResolution();
        double dustY = (j + 0.5)* WORLD.getDustResolution();
        return Math.pow(dustX - position[0],2) + Math.pow((dustY)-position[1],2);
    }

    private void collisionCheck(double[] newPosition){
        boolean collision = false;
        for (double[] wall : WORLD.getEnvironment()) {
            double[] intersect = closestPointOnLine(wall[0], wall[1], wall[2], wall[3], newPosition[0], newPosition[1]);
            double distanceSquared = Math.pow((intersect[0] - newPosition[0]), 2) + Math.pow((intersect[1] - newPosition[1]), 2);
            if (distanceSquared < Math.pow(DIAMETER/2, 2)) {
                collision = true;
                double distance = Math.sqrt(distanceSquared);
                int jumpedWall = lineIntersect(position[0], position[1], newPosition[0], newPosition[1], wall[0], wall[1], wall[2], wall[3]) != null ? -1 : 1;

                newPosition[0] = intersect[0] + (newPosition[0] - intersect[0]) / distance * DIAMETER/2 * jumpedWall;
                newPosition[1] = intersect[1] + (newPosition[1] - intersect[1]) / distance * DIAMETER/2 * jumpedWall;
            }
        }

        if(collision) ticksInWall++;
    }

    private static double[] closestPointOnLine(double x1, double y1, double x2, double y2, double pointX, double pointY) {
        double A = pointX - x1;
        double B = pointY - y1;
        double C = x2 - x1;
        double D = y2 - y1;

        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = -1;
        if (len_sq != 0) //in case of 0 length line
            param = dot / len_sq;

        double xx, yy;

        if (param < 0) {
            xx = x1;
            yy = y1;
        }
        else if (param > 1) {
            xx = x2;
            yy = y2;
        }
        else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }

        return new double[]{xx, yy};
    }

    private static double[] lineIntersect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        double denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
        // Lines are parallel.
        if (denom == 0.0) return null;

        double ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3))/denom;
        double ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3))/denom;
        if (ua >= 0.0f && ua <= 1.0f && ub >= 0.0f && ub <= 1.0f) {
            // Get the intersection point.
            return new double[]{(x1 + ua*(x2 - x1)), (y1 + ua*(y2 - y1))};
        }

        return null;
    }

    public void draw(GraphicsContext g){
        double radius = DIAMETER / 2 * GuiSettings.SCALING;
        double x = position[0] * GuiSettings.SCALING;
        double y = position[1] * GuiSettings.SCALING;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);
    
        //draw body
        g.setFill(GuiSettings.ROBOT_COLOR);
        g.fillOval(x-radius,y-radius,radius*2,radius*2);
        g.setLineWidth(1);
        
        //draw head
        Vector2D v = new Vector2D(angle);
        v.multiply(radius*1);
//        g.setFill(GuiSettings.DIRECTION_COLOR);
//        g.fillOval(x+v.x-radius/2,y+v.y-radius/2,2*radius/2,2*radius/2);
        g.setStroke(GuiSettings.DIRECTION_COLOR);
        g.setLineWidth(2.0);
        g.strokeLine(x, y, x + v.x, y + v.y);

        //draw dust
        if (!DRAWKALMAN){
            //        g.setFill(GuiSettings.DUST_COLOR);
            g.setFill(GuiSettings.ROBOT_COLOR);
            for (int i = 0; i < dustIsCollected.length; i++) {
                for (int j = 0; j < dustIsCollected[i].length; j++) {
                    if(!dustIsCollected[i][j]){
                        g.fillOval((i* WORLD.getDustResolution() + WORLD.getDustResolution()/4)*GuiSettings.SCALING,
                                (j* WORLD.getDustResolution() + WORLD.getDustResolution()/4)*GuiSettings.SCALING, 8, 8);
                    }
                }
            }

            //draw sensor lines
            g.setStroke(GuiSettings.SENSOR_COLOR);
            g.setLineWidth(1.0);

            for (int i = 0; i < NUM_SENSORS; i++) {
                Vector2D v2 = new Vector2D(angle + SENSOR_ANGLES[i]);
                v2.multiply((sensorValues[i] + DIAMETER/2) * GuiSettings.SCALING);
                g.strokeLine(x, y, x + v2.x, y + v2.y);
            }

            //draw sensor values
            for(int i = 0; i < NUM_SENSORS; i++) {
                Vector2D v2 = new Vector2D(angle + SENSOR_ANGLES[i]);
                v2.multiply(DIAMETER * GuiSettings.SCALING);
                g.strokeLine(x,y,x + v2.x,y + v2.y);
                g.strokeText(df.format(sensorValues[i]),x+v2.x,y+v2.y);
            }
        } else {
            double xK = state_guess[0][0] * GuiSettings.SCALING;
            double yK = state_guess[1][0] * GuiSettings.SCALING;
            double angleK = state_guess[2][0];

            //draw Kalman body
            g.setFill(GuiSettings.K_ROBOT_COLOR);
            g.fillOval(xK-radius,yK-radius,radius*2,radius*2);
            g.setLineWidth(1);

            //draw Kalman head
            Vector2D vK = new Vector2D(angleK);
            vK.multiply(radius*1);
    //        g.setFill(GuiSettings.DIRECTION_COLOR);
    //        g.fillOval(x+v.x-radius/2,y+v.y-radius/2,2*radius/2,2*radius/2);
            g.setStroke(GuiSettings.DIRECTION_COLOR);
            g.setLineWidth(2.0);
            g.strokeLine(xK, yK, xK + vK.x, yK + vK.y);

            //draw sensor lines
            g.setStroke(GuiSettings.SENSOR_COLOR);
            g.setLineWidth(1.0);

            for (int i = 0; i < landmarks.length; i++) {
                double x2 = landmarks[i][0] * GuiSettings.SCALING;
                double y2 = landmarks[i][1] * GuiSettings.SCALING;
                g.strokeLine(x, y, x2, y2);
            }
            
        }
    }

    public ANN getANN(){
        return ANN;
    }

    public String getName() {
        return NAME;
    }

    public int getTotalDustCollected() {
        return totalDustCollected;
    }

    public int getTicksInWall() {
        return ticksInWall;
    }

    public void calculateFitness(){
        fitness = FITNESS_WEIGHTS[0] * totalDustCollected * WORLD.getDustResolution() * WORLD.getDustResolution() / WORLD.getWidth() / WORLD.getHeight()
                + FITNESS_WEIGHTS[1] * ticksInWall/totalTicks
                + FITNESS_WEIGHTS[2] * summedTurningPenalty/totalTicks;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public double[] getWheelSpeeds() {
        return wheelSpeeds;
    }

    public void setWheelSpeeds(double leftSpeed, double rightSpeed) {
        if(DO_ACCELERATION){
            wheelSpeeds[0] = clamp(leftSpeed, wheelSpeeds[0] - MAX_ACCELERATION*WORLD.getTimeStep(), wheelSpeeds[0] + MAX_ACCELERATION*WORLD.getTimeStep());
            wheelSpeeds[1] = clamp(rightSpeed, wheelSpeeds[1] - MAX_ACCELERATION*WORLD.getTimeStep(), wheelSpeeds[1] + MAX_ACCELERATION*WORLD.getTimeStep());
        } else {
            wheelSpeeds[0] = leftSpeed;
            wheelSpeeds[1] = rightSpeed;
        }
        clampWheelSpeeds();
    }

    public double[] getVW(){
        return new double[]{velocity, omega};
    }

    public void setVW(double v, double w){
        velocity = v;
        omega = w;
    }

    private void clampWheelSpeeds(){
        wheelSpeeds[0] = clamp(wheelSpeeds[0], -MAX_WHEEL_SPEED, MAX_WHEEL_SPEED);
        wheelSpeeds[1] = clamp(wheelSpeeds[1], -MAX_WHEEL_SPEED, MAX_WHEEL_SPEED);
    }

    private static double clamp(double input, double min, double max){
        return Math.min(Math.max(input, min), max);
    }

    public double getAngle() {
        return angle;
    }

    @Override
    public int compareTo(Robot otherRobot) {
        return Double.compare(otherRobot.getFitness(),this.getFitness());
    }
}
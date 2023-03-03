package com.three.ars_2.simulation;

import com.three.ars_2.gui.GuiSettings;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;

public class World {
    private final double HEIGHT;
    private final double WIDTH;
    private final double[][] ENVIRONMENT;
//    private final ArrayList<Robot> ROBOTS = new ArrayList<>();
    private Population population;

    public World(double width, double height) {
        this.HEIGHT = height;
        this.WIDTH = width;
        this.ENVIRONMENT = createEnvironment();
        double[] startPosition = new double[] {3,3};
        this.population = new Population(this, 1);
//        ROBOTS.add(new Robot(this, startPosition,0.0,"Robot 1"));
//        ROBOTS.add(new Robot(this, startPosition,0.0,"Robot 2"));

    }
    private double[][] createEnvironment() {
        List<double[]> environment = new ArrayList<>();

        environment.add(new double[] {0, HEIGHT, WIDTH, HEIGHT});
        environment.add(new double[] {0, 0, WIDTH, 0});
        environment.add(new double[] {0, 0, 0, HEIGHT});
        environment.add(new double[] {WIDTH, 0, WIDTH, HEIGHT});

        environment.add(new double[] {1, 1, 5, 1});
        environment.add(new double[] {2, 1, 2, 5});
        environment.add(new double[] {0, 2, 1, 2});
        environment.add(new double[] {1, 3, 2, 3});
        environment.add(new double[] {0, 4, 1, 4});
        environment.add(new double[] {1, 5, 5, 5});
        environment.add(new double[] {3, 2, 5, 1});
        environment.add(new double[] {3, 4, 5, 5});
        environment.add(new double[] {4, 3, 6, 2});
        environment.add(new double[] {4, 3, 6, 4});

        return environment.toArray(new double[0][]);
    }

    public void update(double timeStep){
        for(Robot robot: population.getRobots()){
            robot.update(timeStep);
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

    public double[][] getEnvironment(){ return ENVIRONMENT; }

    public List<Robot> getRobots() {
        return population.getRobots();
    }
}

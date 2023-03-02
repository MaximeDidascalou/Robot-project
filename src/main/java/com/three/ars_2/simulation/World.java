package com.three.ars_2.simulation;

import com.three.ars_2.gui.GuiSettings;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;

public class World {
    private final double HEIGHT;
    private final double WIDTH;
    private final double[][] ENVIRONMENT;
    private final ArrayList<Robot> ROBOTS = new ArrayList<>();

    public World(double width, double height) {
        this.HEIGHT = height;
        this.WIDTH = width;
        this.ENVIRONMENT = createEnvironment();
        double[] startPosition = new double[] {5,5};
        ROBOTS.add(new Robot(this, startPosition,0.0,"Robot 1"));
    }
    private double[][] createEnvironment() {
        List<double[]> environment = new ArrayList<>();

        environment.add(new double[] {0, HEIGHT, WIDTH, HEIGHT});
        environment.add(new double[] {0, 0, WIDTH, 0});
        environment.add(new double[] {0, 0, 0, HEIGHT});
        environment.add(new double[] {WIDTH, 0, WIDTH, HEIGHT});

        environment.add(new double[] {1.0, 1.0, 5.0,3.0});
        environment.add(new double[] {1.0, 1.0, 3.0,5.0});

        return environment.toArray(new double[0][]);
    }

    public void draw(GraphicsContext g){
        g.setFill(GuiSettings.backgroundColor);
        g.fillRect(0,0, getWidth()*GuiSettings.SCALING, getHeight()*GuiSettings.SCALING);
        g.setFill(GuiSettings.wall);
        double[][] environment = getEnvironment();
        for(double[] wall : environment){
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
    public ArrayList<Robot> getRobots() {
        return ROBOTS;
    }

}

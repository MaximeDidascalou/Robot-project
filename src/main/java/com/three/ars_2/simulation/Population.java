package com.three.ars_2.simulation;

import java.util.ArrayList;
import java.util.List;

public class Population {
    private final World WORLD;
    private int individualCount;
    private List<Robot> robots = new ArrayList<>();
    public Population(World world, int numIndividuals){
        this.WORLD = world;
        for (int i = 0; i < numIndividuals; i++) {
            robots.add(new Robot(world, "Robot " + i));
        }
        individualCount = numIndividuals;
    }

    public List<Robot> getRobots() {
        return robots;
    }
}

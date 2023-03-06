package com.three.ars_2.simulation;

import com.three.ars_2.gui.Vector2D;

import java.util.ArrayList;

public class PSO {
    static class Random extends java.util.Random{
        public double uniform(double from, double to){
            return (this.nextDouble()*(to-from))+from;
        }
    }

    public static void main(String[] args){
        runPOS(20,100);
    }
    public static void runPOS(int num_particles,int num_iter){
        Vector2D x_range = new Vector2D(-3,3);
        Vector2D y_range = new Vector2D(-3,3);
        double maxSpeed = 0.8;

        Random random = new Random();
        ArrayList<Particle> particles = new ArrayList<>();
        Particle.global_best_pos = new Vector2D(random.uniform(x_range.x, x_range.y), random.uniform(y_range.x, y_range.y));
        for(int i=0;i<num_particles;i++){
            Vector2D pos = new Vector2D(random.uniform(x_range.x, x_range.y), random.uniform(y_range.x, y_range.y));
            Vector2D vel = new Vector2D(0,0);
            particles.add(new Particle(pos,vel,maxSpeed));
        }
        for(int i=0;i<num_iter;i++){
            for(Particle p: particles){
                p.updateVelocity();
            }
            for(Particle p: particles){
                p.updatePosition();
            }
            System.out.println(Particle.global_best_performance);
        }
        for(Particle p: particles){
            for(Vector2D v : p.position_history){
                //System.out.println(v);
            }
        }
    }
}

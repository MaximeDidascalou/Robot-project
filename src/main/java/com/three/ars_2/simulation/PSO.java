package com.three.ars_2.simulation;

import com.three.ars_2.gui.Vector2D;

import java.util.ArrayList;

public class PSO {
    private int num_particles;
    private int num_iter;
    private double max_speed;
    private double a;
    private double b;
    private double c;


    PSO(int num_particles, int num_iter, double max_speed, double a, double b, double c){
        this.num_particles = num_particles;
        this.num_iter = num_iter;
        this.max_speed = max_speed;
        this.a = a;
        this.b = b;
        this.c = c;
    }

    static class Random extends java.util.Random{
        public double uniform(double from, double to){
            return (this.nextDouble()*(to-from))+from;
        }
    }

    public double runPOS(){
        Vector2D x_range = new Vector2D(-3,3);
        Vector2D y_range = new Vector2D(-3,3);
        double maxSpeed = max_speed;


        Random random = new Random();
        ArrayList<Particle> particles = new ArrayList<>();
        Particle.global_best_pos = new Vector2D(random.uniform(x_range.x, x_range.y), random.uniform(y_range.x, y_range.y));
        for(int i=0;i<num_particles;i++){
            Vector2D pos = new Vector2D(random.uniform(x_range.x, x_range.y), random.uniform(y_range.x, y_range.y));
            Vector2D vel = new Vector2D(0,0);
            particles.add(new Particle(a, b, c, pos,vel,maxSpeed));
        }
        for(int i=0;i<num_iter;i++){
            for(Particle p: particles){
                p.updateVelocity();
            }
            for(Particle p: particles){
                p.updatePosition();
            }
            //System.out.println(Particle.global_best_performance);
        }
        return Particle.global_best_performance;
        // for(Particle p: particles){
        //     for(Vector2D v : p.position_history){
        //         //System.out.println(v);
        //     }
        // }
    }

    public void reset(){
        Particle.global_best_performance = Double.MAX_VALUE;
    }
}

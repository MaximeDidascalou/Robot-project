public class Simul {
    void simulate_timestep(World w, char input) {
        double vl = w.r.velLeft;
        double vr = w.r.velRight;
        if (input == 'w'){
            vl = vl + 1;
        }
        else if (input == 's'){
            vl = vl - 1;
        }
        else if (input == 'o'){
            vr = vr + 1;
        }
        else if (input == 'l'){
            vr = vr - 1;
        }
        else if (input == 'x'){
            vr = 0;
            vl = 0;
        }
        else if (input == 't'){
            vr = vr + 1;
            vl = vl + 1;
        }
        else if (input == 'g'){
            vr = vr - 1;
            vl = vl + 1;
        }
        w.r.velLeft = vl;
        w.r.velRight = vr;
        w.r.updatePosition(0.1);
    }
}

public class Robot {
    int num_sensors;
    double width; // diameter or length between wheels
    double[] sensor_angles;
    
    double[] position;
    double vel_right;
    double vel_left;
    double angle;


    Robot(int num_sensors, double width, double[] position,
          double vel_left, double vel_right, double angle){
        this.num_sensors = num_sensors;
        this.width = width;
        this.position = position;
        this.vel_left = vel_left;
        this.vel_right = vel_right;
        this.angle = angle;

        sensor_angles = new double[num_sensors];
        update_sensor_angles();
    }

    void update_sensor_angles() {
        double shift = 360 / num_sensors;
        double total_shift = 0;
        for (int i = 0; i < num_sensors; i++) {
            sensor_angles[i] = angle + total_shift;
            total_shift += shift;
          }
    }
}

public class Main {
    public static void main(String []args) {
        double[] position = {3,3};
        Robot r1 = new Robot(3, 2, position,0,0,12);
        System.out.println(r1.sensor_angles[1]);

        r1.angle = 56;
        r1.update_sensors();
        System.out.println(r1.sensor_angles[1]);

        System.out.println("creating world:");
        World w1 = new World(10,10, r1);
        System.out.println(r1.sensor_values[0]);
        System.out.println(r1.sensor_values[1]);
        System.out.println(r1.sensor_values[2]);
    }
}

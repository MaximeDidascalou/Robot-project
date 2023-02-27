public class Main {
    public static void main(String []args) {
        double[] position = {0.5,0.5};
        Robot r1 = new Robot(3, 2, position,0,0,12);
        System.out.println(r1.sensor_angles[1]);

        r1.angle = 56;
        r1.update_sensor_angles();
        System.out.print(r1.sensor_angles[1]);
    }
}

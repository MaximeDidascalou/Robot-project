public class Main {
    public static void main(String []args) {
        double[] initialPosition = {3.0, 3.0};
        double initialAngle = 0.0;
        Robot r1 = new Robot( initialPosition, initialAngle);

        System.out.println("creating world:");
        World w1 = new World(10,10, r1);
    }
}

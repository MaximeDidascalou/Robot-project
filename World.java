public class World {
    double width;
    double height; // diameter or length between wheels
    Robot r;


    World(double width, double height, Robot r){
        this.width = width;
        this.height = height;
        double[] top = new double[] {height, 0, height, width};
        double[] bottom = new double[] {0, 0, 0, width} ;
        double[] left = new double[] {0, 0, height, 0} ;
        double[] right = new double[] {0, width, height, width} ;

        r.environment = new double[][] {bottom, top, left, right};
        r.update_sensors();
        this.r = r;
    }
}

/*
Class simulation made by Matthew Noack
Simulation ARS2
Need to take input from user to increase or decrease vr, vl (control_input)
Need to create world for robot to traverse (space)
Need to update robot position based on w, r, angle forward and icc (find_next_location)
Need to calculate pathway from current robot location to next robot location (draw_line) 
Need to find next position of robot and make sure that the next position does not hit a wall or go through the wall (find_next_location, draw_line)
*/
import java.util.Scanner; 
import java.lang.Math;
import java.io.*;
import java.util.*;
class simulation{
  double vl = 0;
  double vr = 0;
  double r = 0;
  double next_r = 0;
  double w = 0;
  double next_w = 0;
  double angle_forward = 0;
  int l = 1;
  int[] icc = {0, 0};
  int[] robot_location = {5,5};
  int[] next_robot_location = {5,5};
  int[] sensor_angles = {0, 30, 60, 90, 120, 150, 180, 210, 240, 270, 300, 330};
  double[] senssor_distance = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
  //0 = empty space, 1 = wall, 2 = robot's current location
  int space[][] = {{0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,1,1,1,1,1,1,1,0,0},
                  {0,0,1,0,0,0,0,0,1,0,0},
                  {0,0,1,0,0,0,0,0,1,0,0},
                  {0,0,1,0,0,0,0,0,1,0,0},
                  {0,0,1,0,0,0,0,0,1,0,0},
                  {0,0,1,0,0,0,0,0,1,0,0},
                  {0,0,1,0,0,0,0,0,1,0,0},
                  {0,0,1,1,1,1,1,1,1,0,0},
                  {0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,0,0}};
  void control_input(char input1){
    if (input1 == 'w'){
      vl = vl + 1;
    }
    else if (input1 == 's'){
      vl = vl - 1;
    }
    else if (input1 == 'o'){
      vr = vr + 1;
    }
    else if (input1 == 'l'){
      vr = vr - 1;
    }
    else if (input1 == 'x'){
      vr = 0;
      vl = 0;
    }
    else if (input1 == 't'){
      vr = vr + 1;
      vl = vl + 1;
    }
    else if (input1 == 'g'){
      vr = vr - 1;
      vl = vl + 1;
    }
    else if (input1 == 'q'){
      System.out.println("end rotational input");
    }
    else {
      System.out.println("No input given");
    }
    update_position();
  }
  //If robot hits wall at 90 degree angle, vr = vl = 0, else, robot slides against wall
  double hit_wall(double angle_hit){
    if (angle_hit % 90 == 0){
      vr = 0;
      vl = 0;
      update_rotation();
      angle_forward = angle_hit;
      return angle_forward;
    }
    else{
      angle_forward = (angle_hit + 90) % 360;
      return angle_forward;
    }

  }  
    //updates forward angle when velocity changes
  void update_angle_forward(){
    System.out.println("vl: " + vl + " vr: "+ vr + "\n");
    //if vr > vl, robot turns counterclockwise.
    if (vr > vl){
      angle_forward = (angle_forward + 360 - Math.abs(((vr - vl) * 45)) ) % 360;
      //System.out.println((((Math.abs((vr - vl)) * 30))) % 360);
    }
    //else, robot turns clockwise
    else{
      angle_forward = (angle_forward + 360 + Math.abs(((vr - vl) * 45)) ) % 360;
    }
    //System.out.println("Angle forward: " + angle_forward);
  }
  void update_rotation(){
    w = (vr - vl)/l;
    if ((vr - vl) == 0){
      r = ((vl+vr)/((vr-vl)+1))/2;
    }
    else{
      r = ((vl+vr)/(vr-vl))/2;
    }  
  }
  //finds all x,y points between the robot's current location and the robot's next location. This is done to check if the robot moves past a wall.
  List<int[]> draw_line(int x1, int y1, int x2, int y2) {   
    List<int[]> coordinates = new ArrayList<>();
    int dx = Math.abs(x2 - x1);
    int dy = Math.abs(y2 - y1);
    int sx = x1 < x2 ? 1 : -1;
    int sy = y1 < y2 ? 1 : -1;
    int err = dx - dy;
    while (x1 != x2 || y1 != y2) {
      int[] new_coordinate = {x1, y1};
      coordinates.add(new_coordinate);
      //System.out.println("Draw point at (" + x1 + ", " + y1 + ")");
      int e2 = 2 * err;
      if (e2 > -dy) {
          err -= dy;
          x1 += sx;
      }
      if (e2 < dx) {
          err += dx;
          y1 += sy;
      }
    }
    int[] new_coordinate = {x2, y2};
    coordinates.add(new_coordinate);
    //System.out.println("Draw point at (x2, y2) (" + x2 + ", " + y2 + ")");
    return coordinates;
  }
  //calculates the next position of the robot. If the robot hits a wall or goes through said wall, will hit wall and slide 90 degrees against wall
  void find_next_location(){
    update_angle_forward();
    update_rotation();
    double tempx = robot_location[0] - r*Math.cos(angle_forward);
    double tempy = robot_location[1] + r*Math.sin(angle_forward);
    //System.out.println("tempx: " + tempx + "tempy: " + tempy);
    icc[0] = (int) tempx;
    icc[1] = (int) tempy;
    List<int[]> coordinates = new ArrayList<>();
    coordinates = draw_line(robot_location[0], robot_location[1], icc[0], icc[1]);
    boolean check_wall = false;
    int index = 0;
    int temp_index = 0;
    //Finds if robot hits wall. If it hits wall check_wall is true and makes index equal to the index of the coordinate line of where there is empty space next to the wall, else, it is false.
    for (int[] coordinate : coordinates) {
      if (space[coordinate[0]][coordinate[1]] == 1){
        check_wall = true;
        System.out.println("Hit wall at: (" + coordinate[0] + ", " + coordinate[1] + ")");
        index = temp_index;
        break;
      }
      temp_index = temp_index + 1;
      //System.out.println("(" + coordinate[0] + ", " + coordinate[1] + ")");
    }
    //If check_wall == false, then the robot did not hit a wall, and will go to the last coordinate that the draw_line function found.
    if (check_wall == false){
      space[robot_location[0]][robot_location[1]] = 0;
      robot_location = coordinates.get(coordinates.size() - 1);
      space[robot_location[0]][robot_location[1]] = 2;
    }
    else{
      //if index > 1, robot moved, if not, robot bumping against wall
      if (index > 1){
        space[robot_location[0]][robot_location[1]] = 0;
        robot_location = coordinates.get(index - 1);
        space[robot_location[0]][robot_location[1]] = 2;
      }
      hit_wall(angle_forward);
    }
    System.out.println("Angle forward: " + angle_forward);
  }
  void update_position(){
    find_next_location();
    System.out.print("New robot location x: " + robot_location[0] + " , y: " + robot_location[1] + "\n");
    //space[robot_location[0]][robot_location[1]] = 2;
    for (int i = 0; i < space.length; i++) {
     for (int j = 0; j < space[i].length; j++) {
      System.out.print(space[i][j] + " ");
    }
     System.out.println();
    }
  }
  //Finds distance between robot and point based on distance.
  void find_distance_sensor_angles(){
     int[] temp_robot_location = robot_location;
     double distance_x = 0; //cos(angle)
     double distance_y = 0; //sin(angle)
     
     
  }
}
class Main {
    public static void main(String[] args) {
        //System.out.println("Hello, World!"); 
        /*
        Scanner in = new Scanner(System.in);
        simulation s1 = new simulation();
        System.out.print("Input command: ");
        char user_input = in.next().charAt(0);
        while (user_input != 'q'){
          s1.control_input((char)user_input);
          System.out.print("Input command: ");
          user_input = in.next().charAt(0);
        }
        */
        simulation r1 =  new simulation();
        /*
        r1.control_input('w');
        r1.control_input('w');
        r1.control_input('w');
        r1.control_input('w');
        r1.control_input('s');
        r1.control_input('o');
        r1.control_input('l');
        r1.control_input('x');
        r1.control_input('t');
        r1.control_input('g');
        r1.control_input('q');
        */
        r1.control_input('t');
        r1.control_input('t');
        r1.control_input('t');
        r1.control_input('t');
    }
}

package algorithm;

import map.Map;
import map.WayPoint;
import robot.Robot;
import utils.Orientation;
import utils.Position;

public class Exploration {
    public static final int ROW = 20;
    public static final int COL = 15;
    private Simulator simulator;

    public Exploration(){
    }


    public void sense(Robot robot, Map map){
        Orientation ori = robot.getOrientation();
        Position pos = robot.getPosition();
        Position frontL = robot.getHeadPosition().add(ori.getLeftPosition());
        Position frontC = robot.getHeadPosition();
        Position frontR = robot.getHeadPosition().add(ori.getLeftPosition());

        //if(simulating){


        // }


    }
}

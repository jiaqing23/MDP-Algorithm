package robot;

import map.Map;
import map.WayPointState;
import utils.*;

public class Robot {
    private Position position;
    private Orientation orientation;
    private Map map;

    public Robot(Map map){
        this.map = map;
        this.position = new Position(1,1);
        this.orientation = new Orientation(0); //Up
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public Position getPosition() {
        return position;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public Position getHeadPosition(){
        return orientation.getHeadPosition(position);
    }

    public Position getBackPosition(){
        return orientation.getBackPosition(position);
    }

    public void turnLeft(){
        orientation.turnLeft();
    }

    public void turnRight(){
        orientation.turnRight();
    }

    public void moveForward(){
        Position nextPositon = getHeadPosition();
        if(checkValidPosition(nextPositon)) position = nextPositon;
    }

    public void moveBackward(){
        Position nextPositon = getBackPosition();
        if(checkValidPosition(nextPositon)) position = nextPositon;
    }

    public boolean checkValidPosition(Position position){
        if(map.isBoundary(position))
            return false;

        for(int i = -1; i <= 1; i++)
            for(int j = -1; j <= 1; j++)
                if(map.getMap()[position.x()+i][position.y()+j].getState() == WayPointState.isObstacle)
                    return false;

        return true;
    }

    public static boolean checkValidPosition(Map map, Position position){
        if(map.isBoundary(position))
            return false;

        for(int i = -1; i <= 1; i++)
            for(int j = -1; j <= 1; j++)
                if(map.getMap()[position.x()+i][position.y()+j].getState() == WayPointState.isObstacle)
                    return false;

        return true;
    }
}

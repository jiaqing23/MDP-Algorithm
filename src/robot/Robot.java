package robot;

import map.Map;
import map.WayPointState;
import simulation.GridPanel;
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

    public void turnLeft(){
        orientation.turnLeft();
    }

    public void turnRight(){
        orientation.turnRight();
    }

    public void goStraight(){
        Position nextPositon = getHeadPosition();

        if(map.isBoundary(nextPositon))
            return;

        for(int i = -1; i <= 1; i++)
            for(int j = -1; j <= 1; j++)
                if(map.getMap()[nextPositon.x()+i][nextPositon.y()+j].getState() == WayPointState.isObstacle)
                    return;

        position = nextPositon;
    }
}

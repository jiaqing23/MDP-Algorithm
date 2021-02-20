package mdp.robot;

import mdp.map.Map;
import mdp.map.WayPointState;
import mdp.utils.*;

import java.util.ArrayList;

public class Robot {
    private Position position;
    private Orientation orientation;
    private Map map;
    private ArrayList<RobotAction> bufferedActions;
    private int nextActionIdx; //Pointer on next bufferedActions index

    public Robot(Map map){
        this.map = map;
        this.position = new Position(1,1);
        this.orientation = new Orientation(0); //Up
        this.bufferedActions = new ArrayList<RobotAction>();
        this.nextActionIdx = 0;
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
        return position.add(orientation.getFrontPosition());
    }

    public Position getBackPosition(){
        return position.add(orientation.getBackPosition());
    }

    public void addBufferedAction(RobotAction action){
        this.bufferedActions.add(action);
    }

    public void addBufferedActions(ArrayList<RobotAction> actions){
        for(RobotAction action: actions) this.bufferedActions.add(action);
    }

    public boolean gotRemainingActions() {
        return (nextActionIdx < this.bufferedActions.size());
    }

    public void executeNextAction(){
        if(nextActionIdx < bufferedActions.size()){
            RobotAction nextAction = bufferedActions.get(nextActionIdx);
            switch (nextAction){
                case MoveForward -> moveForward();
                //case MoveBackward -> moveBackward();
                case TurnLeft -> turnLeft();
                case TurnRight -> turnRight();
            }
            nextActionIdx++;
        }
    }

    public void executeRemainingActions(){
        while(nextActionIdx < bufferedActions.size()){
            executeNextAction();
        }
    }

    private void moveForward(){
        Position nextPositon = getHeadPosition();
        if(checkValidPosition(nextPositon)) position = nextPositon;
    }

    private void moveBackward(){
        Position nextPositon = getBackPosition();
        if(checkValidPosition(nextPositon)) position = nextPositon;
    }

    private void turnLeft(){
        orientation.turnLeft();
    }

    private void turnRight(){
        orientation.turnRight();
    }

    public boolean checkValidPosition(Position position){
        for(int i = -1; i <= 1; i++)
            for(int j = -1; j <= 1; j++)
                if(!map.inBoundary(new Position(position.x()+i, position.y()+j)) ||
                        map.getMap()[position.x()+i][position.y()+j].getState() == WayPointState.isObstacle)
                    return false;

        return true;
    }

    public static boolean checkValidPosition(Map map, Position position){
        for(int i = -1; i <= 1; i++)
            for(int j = -1; j <= 1; j++)
                if(!map.inBoundary(new Position(position.x()+i, position.y()+j)) ||
                        map.getMap()[position.x()+i][position.y()+j].getState() == WayPointState.isObstacle)
                    return false;
        return true;
    }
}
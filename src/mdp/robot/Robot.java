package mdp.robot;

import mdp.Main;
import mdp.map.Map;
import mdp.map.WayPointState;
import mdp.utils.Orientation;
import mdp.utils.Position;

import java.util.ArrayList;

public class Robot {
    private Position position;
    private Orientation orientation;
    private Map map;
    private ArrayList<RobotAction> bufferedActions;
    private int nextActionIdx; //Pointer on next bufferedActions index

    private static volatile boolean actionCompleted = false;

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

    public static void setActionCompleted(boolean actionCompleted) {
        Robot.actionCompleted = actionCompleted;
    }

    public void clearBufferedAction(){
        this.bufferedActions.clear();
        nextActionIdx = 0;
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

    public void executeRemainingActions(int executePeriod, boolean waitActionComplete){
        ArrayList<RobotAction> actions;
        actions = new ArrayList<RobotAction>(bufferedActions.subList(nextActionIdx, bufferedActions.size()));

        while(nextActionIdx < bufferedActions.size()){
            if (Main.isSimulating()) {
                try {
                    Thread.sleep(executePeriod);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            executeNextAction();
        }

        if(!Main.isSimulating()){
            actionCompleted = false;
            Main.getRpi().sendPathCommand(actions);
            while(waitActionComplete && !actionCompleted) { //Wait a while and check
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void moveForward(){
        Position nextPositon = getHeadPosition();
        if(checkValidPosition(nextPositon)) position = nextPositon;
        Main.getGui().updateGrid();
    }

    private void moveBackward(){
        Position nextPositon = getBackPosition();
        if(checkValidPosition(nextPositon)) position = nextPositon;
        Main.getGui().updateGrid();
    }

    private void turnLeft(){
        orientation.turnLeft();
        Main.getGui().updateGrid();
    }

    private void turnRight(){
        orientation.turnRight();
        Main.getGui().updateGrid();
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
                        map.getMap()[position.x()+i][position.y()+j].getState() != WayPointState.isEmpty)
                    return false;
        return true;
    }
}

package mdp.algorithm;

import mdp.map.Map;
import mdp.map.WayPointSpecialState;
import mdp.map.WayPointState;
import mdp.robot.Robot;
import mdp.robot.RobotAction;
import mdp.simulation.GUI;
import mdp.utils.Orientation;
import mdp.utils.Position;

import java.util.ArrayList;

public class Exploration {
    public static final int ROW = 20;
    public static final int COL = 15;
    public static final int SHORT_RANGE = 2;
    public static final int LONG_RANGE = 5;

    private Simulator simulator;
    private Robot robot;
    private Map map;
    private GUI gui;
    private int executePeriod;
    private int timeLimit;
    private int coverageLimit;

    public Exploration(GUI gui, Robot robot, Map map, int executePeriod, int timeLimit, int coverageLimit){
        this.robot = robot;
        this.map = map;
        this.gui = gui;
        this.simulator = new Simulator(map);
        this.executePeriod = executePeriod;
        this.timeLimit = timeLimit;
        this.coverageLimit = coverageLimit;
    }

    public Simulator getSimulator() {
        return simulator;
    }

    public void sense(){
        if (robot.gotRemainingActions()) {
            robot.executeRemainingActions(executePeriod);
        }

        SensorData sensor = simulator.getSensorData(robot);

        Orientation ori = robot.getOrientation();
        Position pos = robot.getPosition();
        updateMap(robot.getHeadPosition().add(ori.getLeftPosition()), ori, sensor.frontL, SHORT_RANGE);
        updateMap(robot.getHeadPosition(), ori, sensor.frontM, SHORT_RANGE);
        updateMap(robot.getHeadPosition().add(ori.getRightPosition()), ori, sensor.frontR, SHORT_RANGE);
        updateMap(robot.getHeadPosition().add(ori.getLeftPosition()), ori.getLeftOrientation(), sensor.leftF, SHORT_RANGE);
        updateMap(robot.getBackPosition().add(ori.getLeftPosition()), ori.getLeftOrientation(), sensor.leftB, SHORT_RANGE);
        updateMap(robot.getHeadPosition().add(ori.getRightPosition()), ori.getRightOrientation(), sensor.rightF, LONG_RANGE);
    }

    private int updateMap(Position position, Orientation orientation, int dist, int range){
        Position pos = new Position(position.x(), position.y());
        if(dist == 0) {
            for (int i = 1; i <= range; i++) {
                pos = pos.add(orientation.getFrontPosition());
                map.getMap()[pos.x()][pos.y()].setState(WayPointState.isEmpty);
            }
        }
        else{
            for (int i = 1; i <= dist; i++) {
                pos = pos.add(orientation.getFrontPosition());
                if(i < dist) map.getMap()[pos.x()][pos.y()].setState(WayPointState.isEmpty);
                else if(map.inBoundary(pos)) map.getMap()[pos.x()][pos.y()].setState(WayPointState.isObstacle);
            }
        }
        return 0;
    }

    public Walkable checkWalkable(Orientation orientation){
        WayPointState state1, state2, state3;
        Position posM = robot.getPosition().add(orientation.getFrontPosition().mul(2));
        Position posL = posM.add(orientation.getLeftPosition());
        Position posR = posM.add(orientation.getRightPosition());

        if(!map.inBoundary(posM) || !map.inBoundary(posL) || !map.inBoundary(posR)) return Walkable.No;

        state1 = map.getMap()[posM.x()][posM.y()].getState();
        state2 = map.getMap()[posL.x()][posL.y()].getState();
        state3 = map.getMap()[posR.x()][posR.y()].getState();

        if(state1 == WayPointState.isEmpty && state2 == WayPointState.isEmpty && state3 == WayPointState.isEmpty)
            return Walkable.Yes;
        if(state1 == WayPointState.isObstacle || state2 == WayPointState.isObstacle || state3 == WayPointState.isObstacle)
            return Walkable.No;

        return Walkable.Unknown;
    }

    public static Walkable checkWalkable(Position position, Orientation orientation, Map map){
        WayPointState state1, state2, state3;
        Position posM = position.add(orientation.getFrontPosition().mul(2));
        Position posL = posM.add(orientation.getLeftPosition());
        Position posR = posM.add(orientation.getRightPosition());

        if(!map.inBoundary(posM) || !map.inBoundary(posL) || !map.inBoundary(posR)) return Walkable.No;

        state1 = map.getMap()[posM.x()][posM.y()].getState();
        state2 = map.getMap()[posL.x()][posL.y()].getState();
        state3 = map.getMap()[posR.x()][posR.y()].getState();

        if(state1 == WayPointState.isEmpty && state2 == WayPointState.isEmpty && state3 == WayPointState.isEmpty)
            return Walkable.Yes;
        if(state1 == WayPointState.isObstacle || state2 == WayPointState.isObstacle || state3 == WayPointState.isObstacle)
            return Walkable.No;

        return Walkable.Unknown;
    }

    public void leftWallFollowing(){
        Walkable leftWalkable = checkWalkable(robot.getOrientation().getLeftOrientation());
        sense();
        if(leftWalkable == Walkable.Yes){
            robot.addBufferedAction(RobotAction.TurnLeft);
            sense();
            robot.addBufferedAction(RobotAction.MoveForward);
        }
        else if(leftWalkable == Walkable.No){
            turnRightTillEmpty();
        }
        else if(leftWalkable == Walkable.Unknown){
            robot.addBufferedAction(RobotAction.TurnLeft);
            sense();
            Walkable frontWalkable = checkWalkable(robot.getOrientation());
            if(frontWalkable == Walkable.Yes){
                robot.addBufferedAction(RobotAction.MoveForward);
            }
            else if(frontWalkable == Walkable.No){
                turnRightTillEmpty();
            }
            else{
                System.out.println("Error: Unsure");
            }
        }

    }

    public void turnRightTillEmpty(){
        while(checkWalkable(robot.getOrientation()) == Walkable.No){
            robot.addBufferedAction(RobotAction.TurnRight);
            sense();
        }
        robot.addBufferedAction(RobotAction.MoveForward);
    }

    public ArrayList<Position> getUnexplored(){
        ArrayList<Position> unexplored = new ArrayList<Position>();
        for(int i = 0; i < ROW; i++){
            for(int j = 0; j < COL; j++){
                if(map.getMap()[i][j].getState() == WayPointState.isUnexplored){
                    unexplored.add(new Position(i,j));
                }
            }
        }
        return unexplored;
    }

    public boolean checkTimeUp(){
        String s = gui.getMainFrame().getRightPanel().getTimerPanel().getTimerLabel().getText();
        try{
            String[] time = s.split(":");
            int minute = Integer.parseInt(time[0]);
            int second = Integer.parseInt(time[1]);

            return (minute*60+second >= timeLimit);

        }catch (Exception exception){
            System.out.println(exception.getMessage());
            return true;
        }
    }

    public void solve(){
        for(int i = 0; i < ROW; i++){
            for(int j = 0; j < COL; j++){
                map.getMap()[i][j].setState(WayPointState.isUnexplored);
                map.getMap()[i][j].setSpecialState(WayPointSpecialState.normal);
            }
        }
        for(int i = -1; i <= 1; i++){
            for(int j = -1; j <= 1; j++){
                map.getMap()[map.getStart().x()+i][map.getStart().y()+j].setState(WayPointState.isEmpty);
            }
        }

        gui.updateGrid();
        System.out.println("Exploration Start!");

        boolean reachGoal = false;

        while(!reachGoal || !robot.getPosition().equals(map.getStart())){

            if(robot.getPosition().equals(map.getGoal())) reachGoal = true;

            leftWallFollowing();
            robot.executeRemainingActions(executePeriod);

            ArrayList<Position> unexplored = getUnexplored();
            if((100 - 100*unexplored.size()/(ROW*COL)) > coverageLimit) break;
            if(checkTimeUp()) break;
        }

        if(gui.getMainFrame().getRightPanel().getConfPanel().getTerminationCheckBox().isSelected()) return;

        //Explore Remaining Area
        while(true){
            ArrayList<Position> unexplored = getUnexplored();
            if((100 - 100*unexplored.size()/(ROW*COL)) > coverageLimit) break;
            if(checkTimeUp()) break;

            if(unexplored.size() == 0) break;

            ArrayList<RobotAction> actions = FastestPath.solveExplorationFastestPath(map,
                                                                    robot.getPosition(), robot.getOrientation());

            for(RobotAction action: actions){
                robot.addBufferedAction(action);
                sense();
            }
        }

        ArrayList<RobotAction> actions = FastestPath.solve(map, robot.getPosition(), map.getStart(),
                                                                robot.getOrientation(), new Orientation(0));
        while(actions.size() > 0
                && (actions.get(actions.size()-1) == RobotAction.TurnLeft ||
                    actions.get(actions.size()-1) == RobotAction.TurnRight)){
            actions.remove(actions.size() - 1);
        }
        robot.addBufferedActions(actions);
        robot.executeRemainingActions(executePeriod);
        gui.updateGrid();
        System.out.println("Exploration Done!");
    }

}

package mdp.algorithm;

import mdp.Main;
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
    private FindImage findImage;
    private boolean findImageMode = false;

    private int[][] confidentScore;
    private static final int SHORT1_CONFIDENT = 4;
    private static final int SHORT2_CONFIDENT = 3;
    private static final int LONG_CONFIDENT = 1;


    private static volatile String sensingDataFromRPI;

    public Exploration(GUI gui, Robot robot, Map map, int executePeriod, int timeLimit, int coverageLimit){
        this.robot = robot;
        this.map = map;
        this.gui = gui;
        this.simulator = new Simulator(map);
        this.executePeriod = executePeriod;
        this.timeLimit = timeLimit;
        this.coverageLimit = coverageLimit;
        this.confidentScore = new int[ROW][COL];
    }

    public Simulator getSimulator() {
        return simulator;
    }

    public static void setSensingDataFromRPI(String sensingDataFromRPI) {
        Exploration.sensingDataFromRPI = sensingDataFromRPI;
    }

    public void sense(){
        //TODO: process initial sense
        //if (robot.gotRemainingActions()) {
            robot.executeRemainingActions(executePeriod, true);
        //}

        SensorData sensor = new SensorData();
        if(Main.isSimulating()) sensor = simulator.getSensorData(robot);
        else{
            if(sensingDataFromRPI.isEmpty()){
                System.out.println("Sensor data is empty!");
            }
            else{
                try{
                    sensor.frontL = Integer.parseInt(String.valueOf(sensingDataFromRPI.charAt(0)));
                    sensor.frontM = Integer.parseInt(String.valueOf(sensingDataFromRPI.charAt(1)));
                    sensor.frontR = Integer.parseInt(String.valueOf(sensingDataFromRPI.charAt(2)));
                    sensor.leftF  = Integer.parseInt(String.valueOf(sensingDataFromRPI.charAt(3)));
                    sensor.leftB  = Integer.parseInt(String.valueOf(sensingDataFromRPI.charAt(4)));
                    sensor.rightF = Integer.parseInt(String.valueOf(sensingDataFromRPI.charAt(5)));
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }

        Orientation ori = robot.getOrientation();
        Position pos = robot.getPosition();
        updateMap(robot.getHeadPosition().add(ori.getLeftPosition()), ori, sensor.frontL, SHORT_RANGE);
        updateMap(robot.getHeadPosition(), ori, sensor.frontM, SHORT_RANGE);
        updateMap(robot.getHeadPosition().add(ori.getRightPosition()), ori, sensor.frontR, SHORT_RANGE);
        updateMap(robot.getHeadPosition().add(ori.getLeftPosition()), ori.getLeftOrientation(), sensor.leftF, SHORT_RANGE);
        updateMap(robot.getBackPosition().add(ori.getLeftPosition()), ori.getLeftOrientation(), sensor.leftB, SHORT_RANGE);
        updateMap(robot.getHeadPosition().add(ori.getRightPosition()), ori.getRightOrientation(), sensor.rightF, LONG_RANGE);

        //Robot visited position => high confident empty
        for(int i = -1; i <= 1; i++){
            for(int j = -1; j <= 1; j++){
                confidentScore[pos.x()+i][pos.y()+j] = 100000;
            }
        }

        gui.updateGrid();
    }

    private void setStateByConfident(Position position, WayPointState detectedState, int confidentLevel){
        WayPointState originalState = map.getMap()[position.x()][position.y()].getState();
        if(originalState == WayPointState.isUnexplored){
            confidentScore[position.x()][position.y()] += confidentLevel;
            map.getMap()[position.x()][position.y()].setState(detectedState);
            if(findImageMode){
                if(detectedState == WayPointState.isObstacle) findImage.updateObstacle(position);
                else findImage.removeObstacle(position);
            }
            return;
        }

        if(originalState != detectedState){
            confidentScore[position.x()][position.y()] -= confidentLevel;
            if(confidentScore[position.x()][position.y()] < 0){
                confidentScore[position.x()][position.y()] = -confidentScore[position.x()][position.y()];
                map.getMap()[position.x()][position.y()].setState(detectedState);
                if(findImageMode){
                    if(detectedState == WayPointState.isObstacle) findImage.updateObstacle(position);
                    else findImage.removeObstacle(position);
                }
            }
        }
        else{
            confidentScore[position.x()][position.y()] += confidentLevel;
            if(findImageMode){
                if(detectedState == WayPointState.isObstacle) findImage.updateObstacle(position);
                else findImage.removeObstacle(position);
            }
        }

    }

    private int getConfidentLevel(int d, int range){
        if(range == SHORT_RANGE){
            if(d == 1) return SHORT1_CONFIDENT;
            else return SHORT2_CONFIDENT;
        }
        else return LONG_CONFIDENT;
    }

    private void updateMap(Position position, Orientation orientation, int dist, int range){

        Position pos = new Position(position.x(), position.y());
        if(dist == 0) {
            for (int i = 1; i <= range; i++) {
                pos = pos.add(orientation.getFrontPosition());
                if(!map.inBoundary(pos)) {
                    System.out.println("Sensor error handled");
                    return;
                }

                setStateByConfident(pos, WayPointState.isEmpty, getConfidentLevel(i, range));
            }
        }
        else{
            for (int i = 1; i <= dist; i++) {
                pos = pos.add(orientation.getFrontPosition());

                //Handle sensor error
                if(!map.inBoundary(pos) && i < dist) {
                    System.out.println("Sensor error handled");
                    return;
                }

                if(i < dist) setStateByConfident(pos, WayPointState.isEmpty, getConfidentLevel(i, range));
                else if(map.inBoundary(pos)){
                    setStateByConfident(pos, WayPointState.isObstacle, getConfidentLevel(i, range));
                }
            }
        }
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

    public static boolean canSenseUnknown(Position position, Orientation orientation, Map map){
        WayPointState state1, state2, state3;
        Position posM = position.add(orientation.getFrontPosition().mul(2));
        Position posL = posM.add(orientation.getLeftPosition());
        Position posR = posM.add(orientation.getRightPosition());

        if(map.inBoundary(posM) && map.inBoundary(posL) && map.inBoundary(posR)) {
            state1 = map.getMap()[posM.x()][posM.y()].getState();
            state2 = map.getMap()[posL.x()][posL.y()].getState();
            state3 = map.getMap()[posR.x()][posR.y()].getState();

            if(state1 == WayPointState.isUnexplored || state2 == WayPointState.isUnexplored
                    || state3 == WayPointState.isUnexplored)
                return true;
        }

        posM = position.add(orientation.getLeftPosition().mul(2));
        posL = posM.add(orientation.getLeftOrientation().getLeftPosition());
        posR = posM.add(orientation.getLeftOrientation().getRightPosition());

        if(map.inBoundary(posL) && map.inBoundary(posR)) {
            state2 = map.getMap()[posL.x()][posL.y()].getState();
            state3 = map.getMap()[posR.x()][posR.y()].getState();

            if(state2 == WayPointState.isUnexplored || state3 == WayPointState.isUnexplored)
                return true;
        }

        return false;
    }

    public boolean isLeftEmptyDuringLeftWall(Position position, Orientation orientation){
        Position pos = position.add(orientation.getLeftPosition().mul(2));
        Position dpos = orientation.getFrontPosition();
        for(int i = -2; i <= 2; i++) {
            Position tem = pos.add(dpos.mul(i));
            if (!map.inBoundary(tem) || map.getWayPointState(tem) == WayPointState.isEmpty){
                return false;
            }
        }
        return true;
    }

    public boolean isFrontEmpty(Position position, Orientation orientation){
        Position pos = position.add(orientation.getFrontPosition().mul(2));
        Position dpos = orientation.getLeftPosition();
        for(int i = -2; i <= 2; i++) {
            Position tem = pos.add(dpos.mul(i));
            if (!map.inBoundary(tem) || map.getWayPointState(tem) == WayPointState.isEmpty){
                return false;
            }
        }
        return true;
    }

    public void handleLeftEmptyIssue(){
        if(findImageMode)
            findImage.checkNeedToTakePhotoDuringLeftWall(robot.getPosition(), robot.getOrientation(), RobotAction.TurnLeft);
        robot.addBufferedAction(RobotAction.TurnLeft);
        sense();

        while(!isFrontEmpty(robot.getPosition(), robot.getOrientation())){
            if(findImageMode)
                findImage.checkNeedToTakePhotoDuringLeftWall(robot.getPosition(), robot.getOrientation(), RobotAction.MoveForward);
            robot.addBufferedAction(RobotAction.MoveForward);
            sense();
        }

        if(findImageMode)
            findImage.checkNeedToTakePhotoDuringLeftWall(robot.getPosition(), robot.getOrientation(), RobotAction.MoveForward);
        robot.addBufferedAction(RobotAction.TurnRight);
        sense();
    }

    public void leftWallFollowing(){
        if(isLeftEmptyDuringLeftWall(robot.getPosition(), robot.getOrientation())){
            handleLeftEmptyIssue();
        }

        Walkable leftWalkable = checkWalkable(robot.getOrientation().getLeftOrientation());
        if(leftWalkable == Walkable.Yes){
            if(findImageMode)
                findImage.checkNeedToTakePhotoDuringLeftWall(robot.getPosition(), robot.getOrientation(), RobotAction.TurnLeft);
            robot.addBufferedAction(RobotAction.TurnLeft);
            sense();
            if(findImageMode)
                findImage.checkNeedToTakePhotoDuringLeftWall(robot.getPosition(), robot.getOrientation(), RobotAction.MoveForward);
            robot.addBufferedAction(RobotAction.MoveForward);
            sense();
        }
        else if(leftWalkable == Walkable.No){
            turnRightTillEmpty();
        }
        else if(leftWalkable == Walkable.Unknown){
            robot.addBufferedAction(RobotAction.TurnLeft);
            sense();
            Walkable frontWalkable = checkWalkable(robot.getOrientation());
            if(frontWalkable == Walkable.Yes){
                if(findImageMode)
                    findImage.checkNeedToTakePhotoDuringLeftWall(robot.getPosition(), robot.getOrientation(), RobotAction.MoveForward);
                robot.addBufferedAction(RobotAction.MoveForward);
                sense();
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
            if(findImageMode)
                findImage.checkNeedToTakePhotoDuringLeftWall(robot.getPosition(), robot.getOrientation(), RobotAction.TurnRight);
            robot.addBufferedAction(RobotAction.TurnRight);
            sense();
        }
        if(findImageMode)
            findImage.checkNeedToTakePhotoDuringLeftWall(robot.getPosition(), robot.getOrientation(), RobotAction.MoveForward);
        robot.addBufferedAction(RobotAction.MoveForward);
        sense();
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

        if(!Main.isSimulating()) Main.getRpi().send("AL|AR|S1");
        sense();
        System.out.println("First Sense Done!");
        while(!reachGoal || !robot.getPosition().equals(map.getStart())){

            if(robot.getPosition().equals(map.getGoal())) reachGoal = true;

            leftWallFollowing();
            if(!Main.isSimulating()) Main.getRpi().sendMDFString();

            ArrayList<Position> unexplored = getUnexplored();
            if((100 - 100*unexplored.size()/(ROW*COL)) > coverageLimit) break;
            if(checkTimeUp()) break;
        }

        System.out.println("First round exploration done.");
        if(gui.getMainFrame().getRightPanel().getConfPanel().getTerminationCheckBox().isSelected()) return;

        //Explore Remaining Area
        while(true){
            ArrayList<Position> unexplored = getUnexplored();
            if((100 - 100*unexplored.size()/(ROW*COL)) > coverageLimit) break;
            if(checkTimeUp()) break;

            if(unexplored.size() == 0) break;

            ArrayList<RobotAction> actions = FastestPath.solveExplorationFastestPath(map,
                                                                    robot.getPosition(), robot.getOrientation());

            if(actions.size() == 0){
                System.out.println("Remaining points unreachable");
                break;
            }

            for(RobotAction action: actions){
                robot.addBufferedAction(action);
                sense();
                if(!Main.isSimulating()) Main.getRpi().sendMDFString();
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
        robot.executeRemainingActions(executePeriod, false);
        gui.updateGrid();

        if(!Main.isSimulating()) Main.getRpi().sendMDFString();
        System.out.println("Exploration Done!");
    }

    public void solveForFindImage(FindImage findImage){
        this.findImage = findImage;
        findImageMode = true;

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
        System.out.println("Exploration for Find Image Start!");

        boolean reachGoal = false;

        if(!Main.isSimulating()) Main.getRpi().send("AL|AR|S1");
        sense();
        System.out.println("First Sense Done!");
        while(!reachGoal || !robot.getPosition().equals(map.getStart())){

            if(robot.getPosition().equals(map.getGoal())) reachGoal = true;

            leftWallFollowing();

            ArrayList<Position> unexplored = getUnexplored();
            if((100 - 100*unexplored.size()/(ROW*COL)) > coverageLimit) break;
            if(checkTimeUp()) break;
        }

        //Special case: image at right of the starting point
        if(robot.getPosition().equals(map.getStart())){
            findImage.checkNeedToTakePhotoDuringLeftWall(robot.getPosition(), robot.getOrientation(), RobotAction.TurnRight);
        }

        System.out.println("First round exploration done.");
        if(!Main.isSimulating()) Main.getRpi().sendMDFString();
        if(gui.getMainFrame().getRightPanel().getConfPanel().getTerminationCheckBox().isSelected()) return;

        //Explore Remaining Area
        while(true){
            ArrayList<Position> unexplored = getUnexplored();
            if((100 - 100*unexplored.size()/(ROW*COL)) > coverageLimit) break;
            if(checkTimeUp()) break;

            if(unexplored.size() == 0) break;

            ArrayList<RobotAction> actions = FastestPath.solveExplorationFastestPath(map,
                    robot.getPosition(), robot.getOrientation());

            if(actions.size() == 0){
                System.out.println("Remaining points unreachable");
                break;
            }

            for(RobotAction action: actions){
                robot.addBufferedAction(action);
                sense();
            }
        }

        gui.updateGrid();
        System.out.println("Exploration for Find Image Done!");

        findImageMode = false;
    }

}

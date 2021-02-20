package mdp.algorithm;

import mdp.map.Map;
import mdp.map.WayPoint;
import mdp.map.WayPointState;
import mdp.robot.Robot;
import mdp.robot.RobotAction;
import mdp.utils.Orientation;
import mdp.utils.Position;

public class Exploration {
    public static final int ROW = 20;
    public static final int COL = 15;
    public static final int SHORT_RANGE = 2;
    public static final int LONG_RANGE = 5;
    private Simulator simulator;
    private Robot robot;
    private Map map;

    public Exploration(Robot robot, Map map){
        this.robot = robot;
        this.map = map;
        this.simulator = new Simulator(map);
    }

    public void sense(){
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

    private Walkable checkWalkable(Orientation orientation){
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

    public void LeftWallFollowing(){
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


}

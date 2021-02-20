package mdp.algorithm;

import mdp.map.Map;
import mdp.map.WayPointState;
import mdp.robot.Robot;
import mdp.utils.Orientation;
import mdp.utils.Position;

public class Simulator {
    public static final int ROW = 20;
    public static final int COL = 15;
    private Map cachedMap;

    public static final int SHORT_RANGE = 2;
    public static final int LONG_RANGE = 5;

    public Simulator(Map map){
        cachedMap = new Map();
        for(int i = 0; i < ROW; i++){
            for(int j = 0; j < COL; j++){
                cachedMap.getMap()[i][j].setState(map.getMap()[i][j].getState());
            }
        }
    }

    public SensorData getSensorData(Robot robot){
        SensorData sensor = new SensorData();
        Position pos = robot.getPosition();
        Orientation ori = robot.getOrientation();

        sensor.frontL = detect(robot.getHeadPosition().add(ori.getLeftPosition()), ori, SHORT_RANGE);
        sensor.frontM = detect(robot.getHeadPosition(), ori, SHORT_RANGE);
        sensor.frontR = detect(robot.getHeadPosition().add(ori.getRightPosition()), ori, SHORT_RANGE);
        sensor.leftF  = detect(robot.getHeadPosition().add(ori.getLeftPosition()), ori.getLeftOrientation(), SHORT_RANGE);
        sensor.leftB  = detect(robot.getBackPosition().add(ori.getLeftPosition()), ori.getLeftOrientation(), SHORT_RANGE);
        sensor.rightF = detect(robot.getHeadPosition().add(ori.getRightPosition()), ori.getRightOrientation(), LONG_RANGE);

        return sensor;
    }

    /*
    |----|----|
    |  3 |    |
    |----|----|
    |  2 |    |
    |----|----|
    |  1 |    |
    |----|----|
      ^(position and orientation)
     */
    private int detect(Position position, Orientation orientation, int range){
        Position pos = new Position(position.x(), position.y());

        for(int i = 1; i <= range; i++){
            pos = pos.add(orientation.getFrontPosition());
            if(!cachedMap.inBoundary(pos) || cachedMap.getWayPointState(pos) == WayPointState.isObstacle){
                return i;
            }
        }
        return 0;
    }

}

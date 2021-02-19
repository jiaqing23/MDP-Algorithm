package algorithm;

import map.Map;
import map.WayPointState;
import utils.Orientation;
import utils.Position;

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

    public void getSensorData(){

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
    private int detectShort(Position position, Orientation orientation, int range){
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

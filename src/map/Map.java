package map;


import utils.Position;

public class Map {

    private static final int ROW = 20;
    private static final int COL = 15;

    private WayPoint[][] map;

    public Map(){
        map = new WayPoint[ROW][COL];
        for(int i = 0; i < ROW; i++){
            for(int j = 0; j < COL; j++){
                map[i][j] = new WayPoint(new Position(i, j));
            }
        }
    }

    public WayPoint[][] getMap() {
        return map;
    }

    public boolean isBoundary(Position position){
        return (position.x() == 0 || position.x() == ROW-1 || position.y() == 0 || position.y() == COL-1);
    }
}

package map;

import utils.Position;

public class Map {

    public static final int ROW = 20;
    public static final int COL = 15;
    public static final Position GOAL = new Position(18, 13);
    public static final Position START = new Position(1, 1);


    private MDFString mdfString;
    private WayPoint[][] map;
    private Position FPW;

    public Map(){
        map = new WayPoint[ROW][COL];
        for(int i = 0; i < ROW; i++){
            for(int j = 0; j < COL; j++){
                map[i][j] = new WayPoint(new Position(i, j));
            }
        }

        mdfString = new MDFString();
        FPW = new Position(1,1);
    }

    public Position getStart(){
        return START;
    }

    public Position getGoal(){
        return GOAL;
    }

    public Position getFPW() {
        return FPW;
    }

    public void setFPW(Position FPW) {
        this.FPW = FPW;
    }

    public WayPoint[][] getMap() {
        return map;
    }

    public boolean inBoundary(Position position){
        return (position.x() >= 0 && position.x() <= ROW-1 && position.y() >= 0 && position.y() <= COL-1);
    }

    public WayPointState getWayPointState(Position position){
        return map[position.x()][position.y()].getState();
    }


    public MDFString getMdfString() {
        return mdfString;
    }

    public void updateMapByMDF(){
        String tempS =  mdfString.getMDFBinary();
        tempS = tempS.substring(2,302);
        int count=0;
        for(int i = 0; i < ROW; i++){
            for(int j = 0; j < COL; j++){
                if(tempS.charAt(count)=='1') {
                    map[i][j].setState(WayPointState.isObstacle);
                }
                else{
                    map[i][j].setState(WayPointState.isEmpty);
                }
                count++;
            }
        }
    }

    public void updateMDF(){
        String tempMDF="11";
        int count=0;
        int tempNum;
        for(int i = 0; i < ROW; i++){
            for(int j = 0; j < COL; j++){
                if(map[i][j].getState()!=WayPointState.isEmpty) {
                    tempMDF+="1";
                }else{
                    tempMDF+="0";
                }
            }
        }
        tempMDF+="11";
        mdfString.setMDFBinary(tempMDF);
    }
}

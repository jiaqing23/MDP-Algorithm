package mdp.map;

import mdp.utils.Position;

public class Map {

    public static final int ROW = 20;
    public static final int COL = 15;
    public static final Position GOAL = new Position(18,13);
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
        String mdfString1 =  mdfString.getMDFBinary1().substring(2,302);
        String mdfString2 = mdfString.getMDFBinary2();
        int count1 = 0;
        int count2 = 0;

        for(int i = 0; i < ROW; i++){
            for(int j = 0; j < COL; j++){
                if(mdfString1.charAt(count1) == '1') {
                    if(mdfString2.charAt(count2) == '0') map[i][j].setState(WayPointState.isEmpty);
                    else map[i][j].setState(WayPointState.isObstacle);
                    count2++;
                }
                else{
                    map[i][j].setState(WayPointState.isUnexplored);
                }
                count1++;
            }
        }
    }

    public void updateMDF(){
        String tempMDF = "11";
        for(int i = 0; i < ROW; i++){
            for(int j = 0; j < COL; j++){
                if(map[i][j].getState()==WayPointState.isUnexplored) {
                    tempMDF += "0";
                }
                else{
                    tempMDF += "1";
                }
            }
        }
        tempMDF += "11";
        mdfString.setMDFBinary1(tempMDF);

        tempMDF = "";
        for(int i = 0; i < ROW; i++){
            for(int j = 0; j < COL; j++){
                if(map[i][j].getState()==WayPointState.isEmpty) {
                    tempMDF += "0";
                }
                else if(map[i][j].getState()==WayPointState.isObstacle){
                    tempMDF += "1";
                }
            }
        }
        while((tempMDF.length() %8) != 0){
            tempMDF += "0";
        }
        mdfString.setMDFBinary2(tempMDF);
    }
}

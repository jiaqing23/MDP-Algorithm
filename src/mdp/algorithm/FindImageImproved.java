package mdp.algorithm;

import mdp.Main;
import mdp.map.Map;
import mdp.map.WayPointState;
import mdp.robot.Robot;
import mdp.robot.RobotAction;
import mdp.simulation.GUI;
import mdp.utils.Orientation;
import mdp.utils.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class FindImageImproved {
    public static final int ROW = 20;
    public static final int COL = 15;

    private Robot robot;
    private Map map;
    private GUI gui;
    private int executePeriod;
    private int timeLimit;
    private int coverageLimit;
    private int needTakePhoto[][][] = new int[ROW][COL][4];
    private int candidateEdges[][][] = new int[ROW][COL][4];
    private int middleObstacle[][] = new int[ROW][COL];

    private static volatile int totalImageDetected = 0;

    public FindImageImproved(GUI gui, Robot robot, Map map, int executePeriod, int timeLimit, int coverageLimit){
        this.robot = robot;
        this.map = map;
        this.gui = gui;
        this.executePeriod = executePeriod;
        this.timeLimit = timeLimit;
        this.coverageLimit = coverageLimit;
    }

    public void updateObstacle(Position position){
        for(int k = 0; k < 4; k++){
            Orientation orientation = new Orientation(k);
            Position facingPosition = position.add(orientation.getFrontPosition());

            if(map.inBoundary(facingPosition) && map.getWayPointState(facingPosition) == WayPointState.isEmpty &&
                    candidateEdges[position.x()][position.y()][k] == 0){
                candidateEdges[position.x()][position.y()][k] = 1;
            }
        }
    }

    public void findMiddleObstacle(){
        DisjointSet dsu = new DisjointSet(ROW*COL+1);
        int boundaryId = ROW*COL;

        for(int i = 0; i < ROW; i++){
            for(int j = 0; j < COL; j++){
                Position position = new Position(i, j);
                if(map.getWayPointState(position) == WayPointState.isObstacle) {
                    //Boundary block
                    if(i == 0 || i == ROW-1 || j == 0 || j == COL-1) dsu.unite(boundaryId, position.x()*COL+position.y());

                    for(int k = 0; k < 4; k++){
                        Orientation orientation = new Orientation(k);
                        Position adjPosition = position.add(orientation.getFrontPosition());
                        if(map.inBoundary(adjPosition) && map.getWayPointState(adjPosition) == WayPointState.isObstacle){
                            dsu.unite(adjPosition.x()*COL + adjPosition.y(), position.x()*COL+position.y());
                        }
                    }
                }
            }
        }

        for(int i = 0; i < ROW; i++) {
            for(int j = 0; j < COL; j++){
                Position position = new Position(i, j);
                if(map.getWayPointState(position) == WayPointState.isObstacle
                        && !dsu.same(boundaryId, position.x()*COL+position.y())){
                    middleObstacle[i][j] = 1;
                }
                else middleObstacle[i][j] = 0;
            }
        }
    }

    public ArrayList<State> initializeMapAndGetPositionToTakePhoto(){
        findMiddleObstacle();
        for(int i = 0; i < ROW; i++){
            for(int j = 0; j < COL; j++){
                for(int k = 0; k < 4; k++){
                    needTakePhoto[i][j][k] = 0;

                    Position position = new Position(i, j);
                    Orientation orientation = new Orientation(k);
                    Position facingPosition = position.add(orientation.getFrontPosition());

                    if(map.inBoundary(facingPosition) && middleObstacle[position.x()][position.y()] == 1 &&
                            map.getWayPointState(facingPosition) == WayPointState.isEmpty && candidateEdges[i][j][k] == 0){
                        candidateEdges[i][j][k] = 1;
                        //System.out.println(i + " " + j + " " + k);
                    }
                }
            }
        }

        findNeedTakePhoto(3);
        findNeedTakePhoto(2);
        findNeedTakePhoto(1);

        ArrayList<State> targets = new ArrayList<>();
        for(int i = 0; i < ROW; i++){
            for(int j = 0; j < COL; j++){
                for(int k = 0; k < 4; k++){
                    if(needTakePhoto[i][j][k] == 1){
                        targets.add(new State(new Position(i, j), new Orientation(k)));
                    }
                }
            }
        }
        return targets;
    }

    private void findNeedTakePhoto(int requireWall){
        for(int i = 0; i < ROW; i++){
            for(int j = 0; j < COL; j++){
                Position position = new Position(i, j);
                if(!Robot.checkValidPosition(map, position)) continue;

                for(int k = 0; k < 4; k++){
                    Orientation orientation = new Orientation(k);
                    Orientation cameraOrientation = orientation.getLeftOrientation();
                    Orientation oppositeCameraOrientation = cameraOrientation.getLeftOrientation().getLeftOrientation();

                    int okM = 0, okL = 0, okR = 0;
                    Position posM = position.add(cameraOrientation.getFrontPosition().mul(2));
                    Position posL = posM.add(cameraOrientation.getLeftPosition());
                    Position posR = posM.add(cameraOrientation.getRightPosition());

                    for(int dist = 0; dist < 3; dist++){
                        if(!map.inBoundary(posM) || !map.inBoundary(posL) || !map.inBoundary(posR)) break;

                        int needCheck1 = candidateEdges[posM.x()][posM.y()][oppositeCameraOrientation.getOrientation()];
                        int needCheck2 = candidateEdges[posL.x()][posL.y()][oppositeCameraOrientation.getOrientation()];
                        int needCheck3 = candidateEdges[posR.x()][posR.y()][oppositeCameraOrientation.getOrientation()];

                        if(needCheck1 == 1) okM = 1;
                        if(needCheck2 == 1) okL = 1;
                        if(needCheck3 == 1) okR = 1;

                        if(okM == 0) posM = posM.add(cameraOrientation.getFrontPosition());
                        if(okL == 0) posL = posL.add(cameraOrientation.getFrontPosition());
                        if(okR == 0) posR = posR.add(cameraOrientation.getFrontPosition());
                    }

                    if(okM + okL + okR >= requireWall){
                        //System.out.println("Current pos and ori = "+i + " " + j+ " " + k);
                        needTakePhoto[i][j][k] = 1;
                        if(okM == 1) {
                            candidateEdges[posM.x()][posM.y()][oppositeCameraOrientation.getOrientation()] = -1;
                           // System.out.println("posM x,y = "+ posM.x() + " " + posM.y());
                        }
                        if(okL == 1) {
                            candidateEdges[posL.x()][posL.y()][oppositeCameraOrientation.getOrientation()] = -1;
                           // System.out.println("posL x,y = "+ posL.x() + " " + posL.y());
                        }
                        if(okR == 1) {
                            candidateEdges[posR.x()][posR.y()][oppositeCameraOrientation.getOrientation()] = -1;
                           //System.out.println("posR x,y = "+ posR.x() + " " + posR.y());
                        }
                    }
                }
            }
        }
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

    public void checkNeedToTakePhotoDuringLeftWall(Position position, Orientation orientation, RobotAction nextAction){

        Orientation cameraOrientation = orientation.getLeftOrientation();
        Orientation oppositeCameraOrientation = cameraOrientation.getLeftOrientation().getLeftOrientation();

        ArrayList<Position> inRangePositions = new ArrayList<Position>();
        Position posM = position.add(cameraOrientation.getFrontPosition().mul(2));
        Position posL = posM.add(cameraOrientation.getLeftPosition());
        Position posR = posM.add(cameraOrientation.getRightPosition());
        Position posLF = posL.add(cameraOrientation.getFrontPosition());
        Position posMF =  posM.add(cameraOrientation.getFrontPosition());
        Position posRF =  posR.add(cameraOrientation.getFrontPosition());
        inRangePositions.add(posM);
        inRangePositions.add(posL);
        inRangePositions.add(posR);
        inRangePositions.add(posMF);
        inRangePositions.add(posLF);
        inRangePositions.add(posRF);


        if(nextAction == RobotAction.TurnLeft){
            //Do nothing
        }
        else if(nextAction == RobotAction.TurnRight){
            for(Position pos: inRangePositions){
                if(map.inBoundary(pos) &&
                        candidateEdges[pos.x()][pos.y()][oppositeCameraOrientation.getOrientation()] == 1){

                    for(Position tem: inRangePositions){
                        if(map.inBoundary(tem) &&
                                candidateEdges[tem.x()][tem.y()][oppositeCameraOrientation.getOrientation()] == 1)
                        candidateEdges[tem.x()][tem.y()][oppositeCameraOrientation.getOrientation()] = -1;
                    }

                    System.out.println("Take photo at position " + position + " ,orientation " + orientation);
                    if(!Main.isSimulating()) sendTakePhotoCommand(position, orientation);

                }
            }
        }
        else if(nextAction == RobotAction.MoveForward){
            if((map.inBoundary(posL) &&
                    candidateEdges[posL.x()][posL.y()][oppositeCameraOrientation.getOrientation()] == 1)||
                (map.inBoundary(posLF) &&
                        candidateEdges[posLF.x()][posLF.y()][oppositeCameraOrientation.getOrientation()] == 1)||
                (map.inBoundary(posMF) && map.inBoundary(posR) &&
                        candidateEdges[posMF.x()][posMF.y()][oppositeCameraOrientation.getOrientation()] == 1 &&
                        candidateEdges[posR.x()][posR.y()][oppositeCameraOrientation.getOrientation()] == 1)) {

                for(Position tem: inRangePositions){
                    if(map.inBoundary(tem) &&
                            candidateEdges[tem.x()][tem.y()][oppositeCameraOrientation.getOrientation()] == 1)
                        candidateEdges[tem.x()][tem.y()][oppositeCameraOrientation.getOrientation()] = -1;
                }

                System.out.println("Take photo at position " + position + " ,orientation " + orientation);
                if(!Main.isSimulating()) sendTakePhotoCommand(position, orientation);

            }
        }
    }

    public void sendTakePhotoCommand(Position position, Orientation orientation){
        Orientation cameraOrientation = orientation.getLeftOrientation();
        Position posM = position.add(cameraOrientation.getFrontPosition().mul(2));
        Position posL = posM.add(cameraOrientation.getLeftPosition());
        Position posR = posM.add(cameraOrientation.getRightPosition());
        int dl = -1, dm = -1, dr = -1;

        for(int dist = 1; dist <= 3; dist++){
            if(!map.inBoundary(posM) || !map.inBoundary(posL) || !map.inBoundary(posR)) break;

            if(dl == -1 && map.getWayPointState(posL) == WayPointState.isObstacle) dl = dist;
            if(dm == -1 && map.getWayPointState(posM) == WayPointState.isObstacle) dm = dist;
            if(dr == -1 && map.getWayPointState(posR) == WayPointState.isObstacle) dr = dist;

            posM = posM.add(cameraOrientation.getFrontPosition());
            posL = posL.add(cameraOrientation.getFrontPosition());
            posR = posR.add(cameraOrientation.getFrontPosition());
        }

        Main.getRpi().sendTakePhotoCommand(position, cameraOrientation, dl, dm, dr);
    }

    public void solve(){
        ArrayList<State> targets = initializeMapAndGetPositionToTakePhoto();
        System.out.println("Number of Targets: " + targets.size());

        targets.add(new State(robot.getPosition(), robot.getOrientation()));

        int originId = targets.size()-1;
        ArrayList<Integer> ids = new ArrayList<Integer>();
        int[][] distTable = new int[targets.size()][targets.size()];
        HashMap<Integer, State> idToState = new HashMap<Integer, State>();
        Edge[] edges = new Edge[targets.size() * targets.size()];
        for(int i = 0; i < targets.size(); i++){

            System.out.println(
                    i + ": " + targets.get(i).getPosition().x() + " " + targets.get(i).getPosition().y() + " " + targets.get(i).getOrientation().getOrientation()
            );

            ids.add(i);
            idToState.put(i, targets.get(i));

            int dist[][][] = FastestPath.getFastestDist(map, targets.get(i).getPosition(), targets.get(i).getOrientation());

            for(int j = 0; j < targets.size(); j++) {
                distTable[i][j] = dist[targets.get(j).getPosition().x()][targets.get(j).getPosition().y()]
                        [targets.get(j).getOrientation().getOrientation()];
                if(i == j) distTable[i][j] = Integer.MAX_VALUE;
                edges[i*targets.size()+j] = new Edge(i, j, distTable[i][j]);


                //System.out.println(j + "-" + distTable[i][j]);
            }
        }


        Arrays.sort(edges);

        ArrayList<Integer>[] G = new ArrayList[targets.size()];
        ArrayList<Integer>[] iG = new ArrayList[targets.size()];
        for (int i = 0; i < targets.size(); i++) {
            G[i] = new ArrayList<Integer>();
            iG[i] = new ArrayList<Integer>();
        }

        DisjointSet dsu = new DisjointSet(targets.size());
        for(Edge edge: edges){
            //System.out.println(edge.dist);
            if(!dsu.same(edge.from, edge.to) && G[edge.from].size() ==0 && iG[edge.to].size() == 0){

                dsu.unite(edge.from, edge.to);
                G[edge.from].add(edge.to);
                iG[edge.to].add(edge.from);
            }
        }
        int v1=-1, v2=-1;
        for(int i = 0; i < targets.size(); i++){
            if(G[i].size() == 0) v1 = i;
            if(iG[i].size() == 0) v2 = i;
            if(v1 != -1 && v2 != -1) break;
        }
        G[v1].add(v2);
        iG[v2].add(v1);

        ArrayList<Integer> tour = new ArrayList<Integer>();
        int now = G[originId].get(0);
        while(now != originId){
            tour.add(now);
            now = G[now].get(0);
        }

        System.out.println(tour);
        int totalLength = 0;
        // ArrayList<State> targetSequence = new ArrayList<State>();
        for(int i = 0; i < tour.size(); i++){
            if(checkTimeUp()) break;

            State next = idToState.get(tour.get(i));

            ArrayList<RobotAction> actions = FastestPath.solve(map, robot.getPosition(), next.getPosition(),
                    robot.getOrientation(), next.getOrientation());
            robot.addBufferedActions(actions);
            robot.executeRemainingActions(executePeriod, true);

            if(!Main.isSimulating()) sendTakePhotoCommand(next.getPosition(), next.getOrientation());
            totalLength+=actions.size();
        }

        gui.updateGrid();

        System.out.println(totalLength);

        System.out.println("Find Image Done!");
    }
}

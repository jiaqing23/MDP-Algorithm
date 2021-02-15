package algorithm;

import map.Map;
import map.WayPoint;
import map.WayPointSpecialState;
import map.WayPointState;
import robot.*;
import simulation.GUI;
import utils.Orientation;
import utils.Position;

import java.awt.desktop.SystemEventListener;
import java.util.*;
import java.util.logging.ConsoleHandler;

public class FastestPath {
    private static final int ROTATE_WEIGHT = 1;
    private static final int ROW = 20;
    private static final int COL = 15;

    public FastestPath(){

    }

    public static ArrayList<RobotAction> solve(Map map, Position startPosition, Position endPosition,
                                        Orientation startOrientation, Orientation endOrientation){

        PriorityQueue<Node> pq = new PriorityQueue<Node>(ROW*COL*4, new Node());
        State[][][] states = new State[ROW][COL][4];
        int[][][] dist = new int[ROW][COL][4];
        Transition[][][] from = new Transition[ROW][COL][4];

        for(int i = 0; i < ROW; i++) {
            for(int j = 0; j < COL; j++){
                map.getMap()[i][j].setSpecialState(WayPointSpecialState.normal);

                for(int k = 0; k < 4; k++){
                    states[i][j][k] = new State(map.getMap()[i][j].getPosition(), new Orientation(k));
                    dist[i][j][k] = Integer.MAX_VALUE;
                }
            }
        }

        //Construct States Transition
        for(int i = 0; i < ROW; i++) {
            for(int j = 0; j < COL; j++) {
                WayPoint u = map.getMap()[i][j];
                if(u.getState() == WayPointState.isObstacle || !Robot.checkValidPosition(map, u.getPosition())) continue;

                for(int k = 0; k < 4; k++) {
                    State state = states[i][j][k];

                    //Rotate
                    state.addNeighbour(new Transition(state, states[i][j][state.getOrientation().getRight()],
                                                ROTATE_WEIGHT, RobotAction.RotateRight));
                    state.addNeighbour(new Transition(state, states[i][j][state.getOrientation().getLeft()],
                                                ROTATE_WEIGHT, RobotAction.RotateLeft));

                    //Go straight/back
                    Position front = state.getOrientation().getHeadPosition(state.getPosition());
                    Position back = state.getOrientation().getBackPosition(state.getPosition());
                    if (map.getMap()[front.x()][front.y()].getState() != WayPointState.isObstacle
                            && Robot.checkValidPosition(map, front)) {
                        state.addNeighbour(new Transition(state, states[front.x()][front.y()][k], 1, RobotAction.MoveForward));
                    }
                    if (map.getMap()[back.x()][back.y()].getState() != WayPointState.isObstacle
                            && Robot.checkValidPosition(map, back)) {
                        state.addNeighbour(new Transition(state, states[back.x()][back.y()][k], 1, RobotAction.MoveBackward));
                    }
                }
            }
        }

        pq.add(new Node(states[startPosition.x()][startPosition.y()][startOrientation.getOrientation()], 0));
        dist[startPosition.x()][startPosition.y()][startOrientation.getOrientation()] = 0;

        while (!pq.isEmpty()) {
            Node node = pq.remove();
            State u = node.state;
            int currentDistance = node.cost;

            if (dist[u.getPosition().x()][u.getPosition().y()][u.getOrientation().getOrientation()] < currentDistance) {
                continue;
            }
            for (int i = 0; i < u.getNeighbour().size(); i++) {
                Transition edge = u.getNeighbour().get(i);
                State to = edge.to;
                int cost = edge.cost;

                if (dist[to.getPosition().x()][to.getPosition().y()][to.getOrientation().getOrientation()]
                        > currentDistance + cost) {

                   // if(to.getPosition().x() == 1)
                   //System.out.println(u.getPosition() + " " + u.getOrientation().getOrientation() +
                           // " "+ to.getPosition() + " " + to.getOrientation().getOrientation()+" " +edge.action);

                    dist[to.getPosition().x()][to.getPosition().y()][to.getOrientation().getOrientation()]
                            = currentDistance + cost;
                    from[to.getPosition().x()][to.getPosition().y()][to.getOrientation().getOrientation()] = edge;

                    pq.add(new Node(to,
                            dist[to.getPosition().x()][to.getPosition().y()][to.getOrientation().getOrientation()]));
                }
            }
        }

        ArrayList<RobotAction> actions = new ArrayList<RobotAction>();
        State curr = states[endPosition.x()][endPosition.y()][endOrientation.getOrientation()];
        map.getMap()[curr.getPosition().x()][curr.getPosition().y()].setSpecialState(WayPointSpecialState.isFastestPath);
        while(curr != states[startPosition.x()][startPosition.y()][startOrientation.getOrientation()]){
            Transition edge = from[curr.getPosition().x()][curr.getPosition().y()][curr.getOrientation().getOrientation()];
            actions.add(edge.action);
            curr = edge.from;
            map.getMap()[curr.getPosition().x()][curr.getPosition().y()].setSpecialState(WayPointSpecialState.isFastestPath);
        }

        Collections.reverse(actions);
        System.out.println(actions);
        System.out.println(dist[endPosition.x()][endPosition.y()][endOrientation.getOrientation()]);
        return actions;
    }



    public static void runFastestPath(GUI gui, Robot robot, int executePeriod){
        ArrayList<RobotAction> actions =  solve(gui.getMap(), robot.getPosition(), gui.getMap().GOAL,
                                                robot.getOrientation(), new Orientation(0));

        Timer fastestPathThread = new Timer();

        gui.displayFastestPath();
//
//        fastestPathThread.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if (!actions.isEmpty()) {
//                    gui.getRobot().execute(actions.pop());
//                    gui.update(_gui.getMap(), _gui.getRobot());
//                } else {
//                    System.out.println("Path completed.");
//                    this.cancel();
//                }
//            }
//        }, executePeriod, executePeriod);
    }

}

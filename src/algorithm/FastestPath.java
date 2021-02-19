package algorithm;

import map.Map;
import map.WayPoint;
import map.WayPointSpecialState;
import map.WayPointState;
import robot.*;
import utils.Orientation;
import utils.Position;

import java.util.*;

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
                    state.addNeighbour(new Transition(state, states[i][j][state.getOrientation().getRightOrientation()],
                                                ROTATE_WEIGHT, RobotAction.TurnRight));
                    state.addNeighbour(new Transition(state, states[i][j][state.getOrientation().getLeftOrientation()],
                                                ROTATE_WEIGHT, RobotAction.TurnLeft));

                    //Go straight/back
                    Position front = state.getPosition().add(state.getOrientation().getFrontPosition());
                    Position back = state.getPosition().add(state.getOrientation().getBackPosition());
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
}

package mdp.algorithm;

import mdp.Main;
import mdp.map.Map;
import mdp.map.WayPoint;
import mdp.map.WayPointSpecialState;
import mdp.robot.Robot;
import mdp.robot.RobotAction;
import mdp.utils.Orientation;
import mdp.utils.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

public class FastestPath {
    private static final int ROTATE_WEIGHT = 2;
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

        if(!Main.isSimulating()) Main.getRpi().sendMDFString();

        //Construct States Transition
        for(int i = 0; i < ROW; i++) {
            for(int j = 0; j < COL; j++) {
                WayPoint u = map.getMap()[i][j];
                if(!Robot.checkValidPosition(map, u.getPosition())) continue;
                for(int k = 0; k < 4; k++) {
                    State state = states[i][j][k];

                    //Rotate
                    state.addNeighbour(new Transition(state, states[i][j][state.getOrientation().getRightOrientation().getOrientation()],
                                                ROTATE_WEIGHT, RobotAction.TurnRight));
                    state.addNeighbour(new Transition(state, states[i][j][state.getOrientation().getLeftOrientation().getOrientation()],
                                                ROTATE_WEIGHT, RobotAction.TurnLeft));

                    //Go straight
                    Position front = state.getPosition().add(state.getOrientation().getFrontPosition());
                    if (Robot.checkValidPosition(map, front)) {
                        state.addNeighbour(new Transition(state, states[front.x()][front.y()][k], 1, RobotAction.MoveForward));
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
            if(edge == null) break;
            actions.add(edge.action);
            curr = edge.from;
            map.getMap()[curr.getPosition().x()][curr.getPosition().y()].setSpecialState(WayPointSpecialState.isFastestPath);
        }

        Collections.reverse(actions);
        System.out.println(actions);
        //System.out.println(dist[endPosition.x()][endPosition.y()][endOrientation.getOrientation()]);
        return actions;
    }

    public static ArrayList<RobotAction> solveExplorationFastestPath(Map map, Position startPosition, Orientation startOrientation){

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
                if(!Robot.checkValidPosition(map, u.getPosition())) continue;

                for(int k = 0; k < 4; k++) {
                    State state = states[i][j][k];

                    //Rotate
                    state.addNeighbour(new Transition(state, states[i][j][state.getOrientation().getRightOrientation().getOrientation()],
                            ROTATE_WEIGHT, RobotAction.TurnRight));
                    state.addNeighbour(new Transition(state, states[i][j][state.getOrientation().getLeftOrientation().getOrientation()],
                            ROTATE_WEIGHT, RobotAction.TurnLeft));

                    //Go straight
                    Position front = state.getPosition().add(state.getOrientation().getFrontPosition());
                    if (Robot.checkValidPosition(map, front)) {
                        state.addNeighbour(new Transition(state, states[front.x()][front.y()][k], 1, RobotAction.MoveForward));
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

                    dist[to.getPosition().x()][to.getPosition().y()][to.getOrientation().getOrientation()]
                            = currentDistance + cost;
                    from[to.getPosition().x()][to.getPosition().y()][to.getOrientation().getOrientation()] = edge;

                    pq.add(new Node(to,
                            dist[to.getPosition().x()][to.getPosition().y()][to.getOrientation().getOrientation()]));
                }
            }
        }

        ArrayList<RobotAction> actions = new ArrayList<RobotAction>();

        State target = states[0][0][0];
        int nearestDistance = Integer.MAX_VALUE;
        for(int i = 0; i < ROW; i++) {
            for(int j = 0; j < COL; j++){
                for(int k = 0; k < 4; k++){
                    if(Exploration.canSenseUnknown(states[i][j][k].getPosition(),
                            states[i][j][k].getOrientation(), map)){
                        if(dist[i][j][k] < nearestDistance){
                            nearestDistance = dist[i][j][k];
                            target = states[i][j][k];
                        }
                    }
                }
            }
        }

        if(target == states[0][0][0]) return actions; //Empty

        System.out.println("Next target pos = " + target.getPosition());
        State curr = target;
        map.getMap()[curr.getPosition().x()][curr.getPosition().y()].setSpecialState(WayPointSpecialState.isFastestPath);
        while(curr != states[startPosition.x()][startPosition.y()][startOrientation.getOrientation()]){
            Transition edge = from[curr.getPosition().x()][curr.getPosition().y()][curr.getOrientation().getOrientation()];
            if(edge == null) break;
            actions.add(edge.action);
            curr = edge.from;
            map.getMap()[curr.getPosition().x()][curr.getPosition().y()].setSpecialState(WayPointSpecialState.isFastestPath);
        }

        Collections.reverse(actions);
        System.out.println(actions);
        return actions;
    }

    public static ArrayList<RobotAction> solveFindImagePath(Map map, Position startPosition, Orientation startOrientation,
                                                            ArrayList<State> targets){

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
                if(!Robot.checkValidPosition(map, u.getPosition())) continue;

                for(int k = 0; k < 4; k++) {
                    State state = states[i][j][k];

                    //Rotate
                    state.addNeighbour(new Transition(state, states[i][j][state.getOrientation().getRightOrientation().getOrientation()],
                            ROTATE_WEIGHT, RobotAction.TurnRight));
                    state.addNeighbour(new Transition(state, states[i][j][state.getOrientation().getLeftOrientation().getOrientation()],
                            ROTATE_WEIGHT, RobotAction.TurnLeft));

                    //Go straight
                    Position front = state.getPosition().add(state.getOrientation().getFrontPosition());
                    if (Robot.checkValidPosition(map, front)) {
                        state.addNeighbour(new Transition(state, states[front.x()][front.y()][k], 1, RobotAction.MoveForward));
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

                    dist[to.getPosition().x()][to.getPosition().y()][to.getOrientation().getOrientation()]
                            = currentDistance + cost;
                    from[to.getPosition().x()][to.getPosition().y()][to.getOrientation().getOrientation()] = edge;

                    pq.add(new Node(to,
                            dist[to.getPosition().x()][to.getPosition().y()][to.getOrientation().getOrientation()]));
                }
            }
        }

        State nearestTarget = states[0][0][0];
        int nearestDistance = Integer.MAX_VALUE;
        for(State target: targets){
            Position position = target.getPosition();
            Orientation orientation = target.getOrientation();
            if(dist[position.x()][position.y()][orientation.getOrientation()] <= nearestDistance){
                nearestDistance = dist[position.x()][position.y()][orientation.getOrientation()];
                nearestTarget = target;
            }
        }
        System.out.println("Next target position = " + nearestTarget.getPosition().x() + " "
                + nearestTarget.getPosition().y() + ", orientation = " + nearestTarget.getOrientation().getOrientation());
        targets.remove(nearestTarget);

        ArrayList<RobotAction> actions = new ArrayList<RobotAction>();

        State curr = nearestTarget;
        map.getMap()[curr.getPosition().x()][curr.getPosition().y()].setSpecialState(WayPointSpecialState.isFastestPath);
        while(curr != states[startPosition.x()][startPosition.y()][startOrientation.getOrientation()]){
            Transition edge = from[curr.getPosition().x()][curr.getPosition().y()][curr.getOrientation().getOrientation()];
            if(edge == null) break;
            actions.add(edge.action);
            curr = edge.from;
            map.getMap()[curr.getPosition().x()][curr.getPosition().y()].setSpecialState(WayPointSpecialState.isFastestPath);
        }

        Collections.reverse(actions);
        //System.out.println(actions);
        return actions;
    }

    public static int[][][] getFastestDist(Map map, Position startPosition, Orientation startOrientation){

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
                if(!Robot.checkValidPosition(map, u.getPosition())) continue;

                for(int k = 0; k < 4; k++) {
                    State state = states[i][j][k];

                    //Rotate
                    state.addNeighbour(new Transition(state, states[i][j][state.getOrientation().getRightOrientation().getOrientation()],
                            ROTATE_WEIGHT, RobotAction.TurnRight));
                    state.addNeighbour(new Transition(state, states[i][j][state.getOrientation().getLeftOrientation().getOrientation()],
                            ROTATE_WEIGHT, RobotAction.TurnLeft));

                    //Go straight
                    Position front = state.getPosition().add(state.getOrientation().getFrontPosition());
                    if (Robot.checkValidPosition(map, front)) {
                        state.addNeighbour(new Transition(state, states[front.x()][front.y()][k], 1, RobotAction.MoveForward));
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

                    dist[to.getPosition().x()][to.getPosition().y()][to.getOrientation().getOrientation()]
                            = currentDistance + cost;
                    from[to.getPosition().x()][to.getPosition().y()][to.getOrientation().getOrientation()] = edge;

                    pq.add(new Node(to,
                            dist[to.getPosition().x()][to.getPosition().y()][to.getOrientation().getOrientation()]));
                }
            }
        }

        return dist;
    }
}

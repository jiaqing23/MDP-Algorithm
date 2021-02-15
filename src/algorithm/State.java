package algorithm;

import utils.Orientation;
import utils.Position;

import java.util.ArrayList;

public class State {
    private Position position;
    private Orientation orientation;
    private ArrayList<Transition> neighbour;

    public State(Position position, Orientation orientation){
        this.position = position;
        this.orientation = orientation;
        this.neighbour = new ArrayList<Transition>();
    }

    public void addNeighbour(Transition t) {
        this.neighbour.add(t);
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public Position getPosition() {
        return position;
    }

    public ArrayList<Transition> getNeighbour() {
        return neighbour;
    }
}

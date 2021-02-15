package algorithm;

import java.util.Comparator;

class Node implements Comparator<Node> {
    public State state;
    public int cost;

    public Node() {

    }

    public Node(State state, int cost)
    {
        this.state = state;
        this.cost = cost;
    }

    @Override
    public int compare(Node node1, Node node2)
    {
        if (node1.cost < node2.cost)
            return -1;
        else if (node1.cost > node2.cost)
            return 1;
        else if(node1.state.getOrientation().getOrientation() < node2.state.getOrientation().getOrientation() ){
            return -1;
        }
        else if(node1.state.getOrientation().getOrientation() > node2.state.getOrientation().getOrientation() ){
        return 1;
    }
        return 0;
    }
}

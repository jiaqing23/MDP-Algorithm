package mdp.algorithm;

public class Edge implements Comparable<Edge> {

    public int from;
    public int to;
    public int dist;

    public Edge(int a, int b, int dist) {
        this.from = a;
        this.to = b;
        this.dist = dist;
    }

    @Override
    public int compareTo(Edge e2) {
        if(this.dist < e2.dist) return -1;
        else if(this.dist == e2.dist) return 0;
        else return 1;
    }

}
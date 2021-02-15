package utils;

public class Position {
    private int x, y;

    public Position(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public void x(int x) {
        this.x = x;
    }

    public void y(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return x + ", " + y ;
    }

    public boolean equals(Position coord) {
        return coord.x() == x && coord.y() == y;
    }

    public void add(Position coord) {
        x += coord.x();
        y += coord.y();
    }


}

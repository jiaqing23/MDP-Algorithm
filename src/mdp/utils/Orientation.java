package mdp.utils;

public class Orientation {
    private int orientation;
    /*
         0
       3 + 1
         2
     */

    public Orientation(){
        this.orientation = 0;
    }

    public Orientation(int x){
        this.orientation = x;
    }

    @Override
    public String toString() {
        return String.valueOf(orientation);
    }

    public int getOrientation() {
        return orientation;
    }

    public void turnLeft(){
        orientation = (orientation+3)%4;
    }

    public void turnRight(){
        orientation = (orientation+1)%4;
    }

    public Orientation getLeftOrientation(){
        return new Orientation((orientation+3)%4);
    }

    public Orientation getRightOrientation(){
        return new Orientation((orientation+1)%4);
    }

    public Position getFrontPosition(){
        switch (orientation){
            case 0:
                return new Position(1, 0);
            case 1:
                return new Position(0, 1);
            case 2:
                return new Position(-1, 0);
            case 3:
                return new Position(0, -1);
            default:
                return new Position(0, 0);
        }
    }

    public Position getBackPosition(){
        switch (orientation){
            case 0:
                return new Position(-1, 0);
            case 1:
                return new Position(0, -1);
            case 2:
                return new Position(1, 0);
            case 3:
                return new Position(0, 1);
            default:
                return new Position(0, 0);
        }
    }

    public Position getLeftPosition(){
        switch (orientation){
            case 0:
                return new Position(0, -1);
            case 1:
                return new Position(1, 0);
            case 2:
                return new Position(0, 1);
            case 3:
                return new Position(-1, 0);
            default:
                return new Position(0, 0);
        }
    }

    public Position getRightPosition(){
        switch (orientation){
            case 0:
                return new Position(0, 1);
            case 1:
                return new Position(-1, 0);
            case 2:
                return new Position(0, -1);
            case 3:
                return new Position(1, 0);
            default:
                return new Position(0, 0);
        }
    }
}

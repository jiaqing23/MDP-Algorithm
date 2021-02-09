package utils;

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

    public void turnLeft(){
        orientation = (orientation+3)%4;
    }

    public void turnRight(){
        orientation = (orientation+1)%4;
    }

    public Position getHeadPosition(Position position){
        switch (orientation){
            case 0:
                return new Position(position.x()+1, position.y());
            case 1:
                return new Position(position.x(), position.y()+1);
            case 2:
                return new Position(position.x()-1, position.y());
            case 3:
                return new Position(position.x(), position.y()-1);
            default:
                return position;
        }
    }
}

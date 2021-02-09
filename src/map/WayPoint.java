package map;

import utils.Position;

public class WayPoint {
    private Position position;
    private WayPointState state;

    public WayPoint(Position position){
        this.position = position;
        this.state = WayPointState.isEmpty;
    }

    public Position getPosition() {
        return position;
    }

    public WayPointState getState() {
        return state;
    }

    public void setState(WayPointState state) {
        this.state = state;
    }
}

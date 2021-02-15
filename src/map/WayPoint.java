package map;

import utils.Position;

public class WayPoint {
    private Position position;
    private WayPointState state;
    private WayPointSpecialState specialState;

    public WayPoint(Position position){
        this.position = position;
        this.state = WayPointState.isEmpty;
        this.specialState = WayPointSpecialState.normal;
    }

    public Position getPosition() {
        return position;
    }

    public WayPointState getState() {
        return state;
    }

    public WayPointSpecialState getSpecialState() {
        return specialState;
    }

    public void setState(WayPointState state) {
        this.state = state;
    }

    public void setSpecialState(WayPointSpecialState specialState) {
        this.specialState = specialState;
    }
}

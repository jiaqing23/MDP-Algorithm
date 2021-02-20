package mdp.simulation.views;

import javax.swing.*;
import java.awt.*;

import mdp.map.WayPoint;
import mdp.map.WayPointState;
import mdp.utils.Position;

public class GridSquare extends JPanel {

    private Position position;
    private WayPoint wayPoint;

    public GridSquare(Position position){
        this.position = position;

        JLabel label = new JLabel(position.toString());
        label.setForeground(Color.black);
        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 7));
        this.setBackground(Color.white);
        this.add(label);
    }


    public void toggleObstacle() {
        if(this.wayPoint.getState() == WayPointState.isObstacle){
            this.setBackground(Color.white);
            this.wayPoint.setState(WayPointState.isEmpty);
        }
        else{
            this.setBackground(Color.black);
            this.wayPoint.setState(WayPointState.isObstacle);
        }
    }

    public Position getPosition() {
        return position;
    }

    public void setWayPoint(WayPoint wayPoint) {
        this.wayPoint = wayPoint;
    }

    public WayPoint getWayPoint() {
        return wayPoint;
    }

}

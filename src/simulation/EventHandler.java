package simulation;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EventHandler {
    private GUI gui;
    private GridPanel gridPanel;

    public EventHandler(GUI gui){
        this.gui = gui;
        this.gridPanel = gui.getMainFrame().getLeftPanel().getGridPanel();

        gui.getMainFrame().getRightPanel().getMotionPanel().getTurnLeftButton().
                addMouseListener(wrapMouseAdapter(MouseClickEvent.TurnLeft));
        gui.getMainFrame().getRightPanel().getMotionPanel().getTurnRightButton().
                addMouseListener(wrapMouseAdapter(MouseClickEvent.TurnRight));
        gui.getMainFrame().getRightPanel().getMotionPanel().getGoStraightButton().
                addMouseListener(wrapMouseAdapter(MouseClickEvent.GoStraight));
    }

    private MouseAdapter wrapMouseAdapter(MouseClickEvent event) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switch(event){
                    case TurnLeft:
                        turnLeft(e);
                        break;
                    case GoStraight:
                        goStraight(e);
                        break;
                    case TurnRight:
                        turnRight(e);
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void turnLeft(MouseEvent e){
        gui.getRobot().turnLeft();
        gridPanel.updateGrid();
    }

    private void goStraight(MouseEvent e){
        gui.getRobot().goStraight();
        gridPanel.updateGrid();
    }

    private void turnRight(MouseEvent e){
        gui.getRobot().turnRight();
        gridPanel.updateGrid();
    }


}

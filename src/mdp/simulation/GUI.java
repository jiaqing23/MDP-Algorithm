package mdp.simulation;

import mdp.map.Map;
import mdp.robot.Robot;
import mdp.simulation.views.MainFrame;

public class GUI{

    private MainFrame mainFrame;
    private EventHandler eventHandler;
    private Robot robot;
    private Map map;

    public GUI(Robot robot, Map map) {
        this.robot = robot;
        this.map = map;

        mainFrame = new MainFrame();
        eventHandler = new EventHandler(this);

        this.mainFrame.getLeftPanel().getGridPanel().setRobotAndMap(robot, map);

        //To be removed
        this.updateGrid();
    }

    public MainFrame getMainFrame() {
        return mainFrame;
    }

    public Robot getRobot() {
        return robot;
    }

    public Map getMap() {
        return map;
    }

    public void updateGrid(){this.mainFrame.getLeftPanel().getGridPanel().updateGrid();}
}
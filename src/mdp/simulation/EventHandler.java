package mdp.simulation;

import mdp.Main;
import mdp.algorithm.Exploration;
import mdp.algorithm.FastestPath;
import mdp.map.WayPointSpecialState;
import mdp.robot.Robot;
import mdp.robot.RobotAction;
import mdp.utils.Orientation;
import mdp.utils.Position;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.*;

public class EventHandler {
    private GUI gui;
    private Robot robot;
    private Timer fastestPathThread;
    private Timer timerThread;
    private Thread explorationThread;
    private Exploration exploration;

    public EventHandler(GUI gui){
        this.gui = gui;
        this.robot = gui.getRobot();

        gui.getMainFrame().getRightPanel().getMotionPanel().getTurnLeftButton().
                addMouseListener(wrapMouseAdapter(MouseClickEvent.TurnLeft));
        gui.getMainFrame().getRightPanel().getMotionPanel().getTurnRightButton().
                addMouseListener(wrapMouseAdapter(MouseClickEvent.TurnRight));
        gui.getMainFrame().getRightPanel().getMotionPanel().getMoveForwardButton().
                addMouseListener(wrapMouseAdapter(MouseClickEvent.MoveForward));
//        gui.getMainFrame().getRightPanel().getMotionPanel().getMoveBackwardButton().
//                addMouseListener(wrapMouseAdapter(MouseClickEvent.MoveBackward));
        gui.getMainFrame().getRightPanel().getMdfPanel().getExportButton().
                addMouseListener(wrapMouseAdapter(MouseClickEvent.ExportMDF));
        gui.getMainFrame().getRightPanel().getMdfPanel().getImportButton().
                addMouseListener(wrapMouseAdapter(MouseClickEvent.ImportMDF));
        gui.getMainFrame().getRightPanel().getMdfPanel().getGetStringButton().
                addMouseListener(wrapMouseAdapter(MouseClickEvent.GetMDFString));
        gui.getMainFrame().getRightPanel().getTaskPanel().getFastestPathButton().
                addMouseListener(wrapMouseAdapter(MouseClickEvent.RunFastestPath));
        gui.getMainFrame().getRightPanel().getTaskPanel().getExplorationButton().
                addMouseListener(wrapMouseAdapter(MouseClickEvent.RunExploration));
        gui.getMainFrame().getRightPanel().getTaskPanel().getFindImageButton().
                addMouseListener(wrapMouseAdapter(MouseClickEvent.RunFindImage));
        gui.getMainFrame().getRightPanel().getConfPanel().getSimulationCheckBox().
                addMouseListener(wrapMouseAdapter(MouseClickEvent.ToggleSimulation));
        gui.getMainFrame().getRightPanel().getTaskPanel().getStopButton().
                addMouseListener(wrapMouseAdapter(MouseClickEvent.Stop));
        gui.getMainFrame().getRightPanel().getHardwarePanel().getConnectRPIButton().
                addMouseListener(wrapMouseAdapter(MouseClickEvent.ConnectRPI));

        gui.getMainFrame().getRightPanel().getMdfPanel().getTestingButton().
                addMouseListener(wrapMouseAdapter(MouseClickEvent.Testing));
    }

    private MouseAdapter wrapMouseAdapter(MouseClickEvent event) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                resolveEvent(event);
            }
        };
    }

    public void resolveEvent(MouseClickEvent event){
        switch(event){
            case TurnLeft -> turnLeft();
            case MoveForward -> moveForward();
            case TurnRight -> turnRight();
            //case MoveBackward -> moveBackward(e);
            case ImportMDF -> importMDF();
            case ExportMDF -> exportMDF();
            case GetMDFString -> getMDFString();
            case RunFastestPath -> runFastestPath();
            case RunExploration -> runExploration();
            case RunFindImage -> runFindImage();
            case ToggleSimulation -> toggleSimulation();
            case Stop -> stop();
            case ConnectRPI -> connectRPI();
            case Testing -> testing();
        }
    }

    private void toggleSimulation() {
        boolean isSelected = gui.getMainFrame().getRightPanel().getConfPanel().getSimulationCheckBox().isSelected();
        Main.setSimulating(isSelected);
        if (isSelected) {
            System.out.println("Simulation On.");
        } else {
            System.out.println("Simulation Off.");
        }
    }

    private void testing(){
        gui.updateGrid();
    }

    private void turnLeft(){
        gui.getRobot().addBufferedAction(RobotAction.TurnLeft);
        gui.getRobot().executeNextAction();
    }

    private void moveForward(){
        gui.getRobot().addBufferedAction(RobotAction.MoveForward);
        gui.getRobot().executeNextAction();
    }

    private void turnRight(){
        gui.getRobot().addBufferedAction(RobotAction.TurnRight);
        gui.getRobot().executeNextAction();
    }

//    private void moveBackward(MouseEvent e){
//        gui.getRobot().addBufferedAction(RobotAction.MoveBackward);
//        gui.getRobot().executeNextAction();
//    }

    private void connectRPI(){
        Main.connectToRpi();
    }

    public void importMDF() {
        try {
            File myObj = new File("MDF.txt");
            Scanner myReader = new Scanner(myObj);

            String data = myReader.nextLine();
            System.out.println(data);
            gui.getMap().getMdfString().setMDFHex1(data);
            data = myReader.nextLine();
            System.out.println(data);
            gui.getMap().getMdfString().setMDFHex2(data);
            gui.getMap().updateMapByMDF();
            gui.updateGrid();
            JOptionPane.showMessageDialog(null,"Import successfully.",
                    "Import",JOptionPane.INFORMATION_MESSAGE);

            myReader.close();
        } catch (FileNotFoundException f) {
            System.out.println("importMDF failed.");
            f.printStackTrace();
        }
    }

    public void exportMDF() {
        try {
            FileWriter myWriter = new FileWriter("MDF.txt");
            gui.getMap().updateMDF();
            myWriter.write(gui.getMap().getMdfString().getMDFHex1() + "\n" + gui.getMap().getMdfString().getMDFHex2());
            myWriter.close();
            JOptionPane.showMessageDialog(null,"Export successfully.",
                    "Export",JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException f) {
            System.out.println("exportMDF failed.");
            f.printStackTrace();
        }
    }

    public void getMDFString(){
        JTextArea textArea = new JTextArea(6, 25);
        gui.getMap().updateMDF();
        textArea.setText(gui.getMap().getMdfString().getMDFHex1() + "\n" + gui.getMap().getMdfString().getMDFHex2());
        JOptionPane.showMessageDialog(null, textArea,
                                     "MDF",JOptionPane.INFORMATION_MESSAGE);
    }

    public void startTimer(){
        if(timerThread != null) stopTimer();
        gui.getMainFrame().getRightPanel().getTimerPanel().getTimerLabel().setText("00:00");

        Date startTime = new Date();
        timerThread = new Timer();
        timerThread.schedule(new TimerTask() {
            @Override
            public void run() {
                Date diffTime = new Date(new Date().getTime() - startTime.getTime() - 1800000);
                String timeStr = new SimpleDateFormat("mm:ss").format(diffTime);
                gui.getMainFrame().getRightPanel().getTimerPanel().getTimerLabel().setText(timeStr);
            }
        }, 1000, 1000);
    }

    public void stopTimer(){
        timerThread.cancel();
    }

    public void stop(){
        if(timerThread != null) stopTimer();
        gui.getMainFrame().getRightPanel().getTimerPanel().getTimerLabel().setText("00:00");

        if(explorationThread != null) explorationThread.stop();
        if(fastestPathThread != null) fastestPathThread.cancel();

        robot.setPosition(gui.getMap().getStart());
        robot.setOrientation(new Orientation(0));

        for(int i = 0; i < gui.getMap().ROW; i++){
            for(int j = 0; j < gui.getMap().COL; j++){
                gui.getMap().getMap()[i][j].setSpecialState(WayPointSpecialState.normal);

                if(Main.isSimulating() && exploration != null){
                    gui.getMap().getMap()[i][j].setState(
                            exploration.getSimulator().getCachedMap().getMap()[i][j].getState()
                    );
                }
            }
        }

        gui.updateGrid();
    }

    public void runFastestPath() {
        int executePeriod;
        Position FPW;
        try{
            executePeriod = Integer.parseInt(gui.getMainFrame().getRightPanel().getConfPanel()
                                                            .getSimulationSpeedTextField().getText().trim());
            FPW = new Position(Integer.parseInt(gui.getMainFrame().getRightPanel().getConfPanel()
                                .getWaypointXTextField().getText().trim()),
                                Integer.parseInt(gui.getMainFrame().getRightPanel().getConfPanel()
                                .getWaypointYTextField().getText().trim()));
            if(robot.checkValidPosition(FPW)) gui.getMap().setFPW(FPW);
            else{
                System.out.println("FPW Invalid!");
                return;
            }
        }catch(Exception exception){
            System.out.println(exception.getMessage());
            return;
        }

        startTimer();

        for(int i = 0; i < gui.getMap().ROW; i++)
            for(int j = 0; j < gui.getMap().COL; j++)
                gui.getMap().getMap()[i][j].setSpecialState(WayPointSpecialState.normal);

        ArrayList<RobotAction> actions = FastestPath.solve(gui.getMap(), robot.getPosition(), gui.getMap().getFPW(),
                                                            robot.getOrientation(), new Orientation(0));
        ArrayList<RobotAction> actions2 = FastestPath.solve(gui.getMap(), gui.getMap().getFPW(), gui.getMap().getGoal(),
                                                            new Orientation(0), new Orientation(0));


        while(actions.size() > 0 && actions2.size() > 0 &&
                ((actions.get(actions.size()-1) == RobotAction.TurnLeft && actions2.get(0) == RobotAction.TurnRight) ||
                 (actions.get(actions.size()-1) == RobotAction.TurnRight && actions2.get(0) == RobotAction.TurnLeft))){
            actions.remove(actions.size() - 1);
            actions2.remove(0);
        }

        robot.addBufferedActions(actions);
        robot.addBufferedActions(actions2);

        gui.updateGrid();

        fastestPathThread = new Timer();
        fastestPathThread.schedule(new TimerTask() {
            @Override
            public void run() {
                if (robot.gotRemainingActions()) {
                    robot.executeNextAction();
                } else {
                    System.out.println("Path completed.");
                    this.cancel();
                    stopTimer();
                }
            }
        }, executePeriod, executePeriod);
    }

    public void runExploration(){
        int executePeriod, timeLimit, coverageLimit;
        try{
            executePeriod = Integer.parseInt(gui.getMainFrame().getRightPanel().getConfPanel()
                    .getSimulationSpeedTextField().getText().trim());
            timeLimit = Integer.parseInt(gui.getMainFrame().getRightPanel().getConfPanel()
                    .getTimeLimitTextField().getText().trim());
            coverageLimit = Integer.parseInt(gui.getMainFrame().getRightPanel().getConfPanel()
                    .getCoverageLimitTextField().getText().trim());
        }catch(Exception exception){
            System.out.println(exception.getMessage());
            return;
        }

        startTimer();

        exploration = new Exploration(gui, robot, gui.getMap(), executePeriod, timeLimit, coverageLimit);
        explorationThread = new Thread(() -> {
            try {
                exploration.solve();
                stopTimer();
                explorationThread.stop();
            } catch(Exception exception){
                System.out.println(exception.getMessage());
                return;
            }
        });
        explorationThread.start();
    }

    public void runFindImage(){
        int executePeriod, timeLimit, coverageLimit;
        try{
            executePeriod = Integer.parseInt(gui.getMainFrame().getRightPanel().getConfPanel()
                    .getSimulationSpeedTextField().getText().trim());
            timeLimit = Integer.parseInt(gui.getMainFrame().getRightPanel().getConfPanel()
                    .getTimeLimitTextField().getText().trim());
            coverageLimit = Integer.parseInt(gui.getMainFrame().getRightPanel().getConfPanel()
                    .getCoverageLimitTextField().getText().trim());
        }catch(Exception exception){
            System.out.println(exception.getMessage());
            return;
        }

        startTimer();

        exploration = new Exploration(gui, robot, gui.getMap(), executePeriod, timeLimit, coverageLimit);
        explorationThread = new Thread(() -> {
            try {
                exploration.solve();
                stopTimer();
                explorationThread.stop();
            } catch(Exception exception){
                System.out.println(exception.getMessage());
                return;
            }
        });
        explorationThread.start();
    }

}

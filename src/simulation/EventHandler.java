package simulation;

import algorithm.FastestPath;
import map.WayPointSpecialState;
import robot.Robot;
import robot.RobotAction;
import simulation.views.GridPanel;
import utils.Orientation;
import utils.Position;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class EventHandler {
    private GUI gui;
    private Robot robot;
    private Timer fastestPathThread;
    private Thread explorationThread;

    public EventHandler(GUI gui){
        this.gui = gui;
        this.robot = gui.getRobot();
        fastestPathThread = new Timer();
        explorationThread = new Thread();

        gui.getMainFrame().getRightPanel().getMotionPanel().getTurnLeftButton().
                addMouseListener(wrapMouseAdapter(MouseClickEvent.TurnLeft));
        gui.getMainFrame().getRightPanel().getMotionPanel().getTurnRightButton().
                addMouseListener(wrapMouseAdapter(MouseClickEvent.TurnRight));
        gui.getMainFrame().getRightPanel().getMotionPanel().getMoveForwardButton().
                addMouseListener(wrapMouseAdapter(MouseClickEvent.MoveForward));
        gui.getMainFrame().getRightPanel().getMotionPanel().getMoveBackwardButton().
                addMouseListener(wrapMouseAdapter(MouseClickEvent.MoveBackward));
        gui.getMainFrame().getRightPanel().getMdfPanel().getExportButton().
                addMouseListener(wrapMouseAdapter(MouseClickEvent.ExportMDF));
        gui.getMainFrame().getRightPanel().getMdfPanel().getImportButton().
                addMouseListener(wrapMouseAdapter(MouseClickEvent.ImportMDF));
        gui.getMainFrame().getRightPanel().getMdfPanel().getGetStringButton().
                addMouseListener(wrapMouseAdapter(MouseClickEvent.GetMDFString));
        gui.getMainFrame().getRightPanel().getTaskPanel().getFastestPathButton().
                addMouseListener(wrapMouseAdapter(MouseClickEvent.RunFastestPath));
        gui.getMainFrame().getRightPanel().getMdfPanel().getTestingButton().
                addMouseListener(wrapMouseAdapter(MouseClickEvent.Testing));
    }

    private MouseAdapter wrapMouseAdapter(MouseClickEvent event) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switch(event){
                    case TurnLeft:
                        turnLeft(e);
                        break;
                    case MoveForward:
                        moveForward(e);
                        break;
                    case TurnRight:
                        turnRight(e);
                        break;
                    case MoveBackward:
                        moveBackward(e);
                        break;
                    case ImportMDF:
                        importMDF(e);
                        break;
                    case ExportMDF:
                        exportMDF(e);
                        break;
                    case GetMDFString:
                        getMDFString(e);
                        break;
                    case RunFastestPath:
                        runFastestPath(e);
                        break;
                    case Testing:
                        testing(e);
                    default:
                        break;
                }
            }
        };
    }

    private void testing(MouseEvent e){
        gui.updateGrid();
    }

    private void turnLeft(MouseEvent e){
        gui.getRobot().addBufferedAction(RobotAction.TurnLeft);
        gui.getRobot().executeRemainingActions();
        gui.updateGrid();
    }

    private void moveForward(MouseEvent e){
        gui.getRobot().addBufferedAction(RobotAction.MoveForward);
        gui.getRobot().executeRemainingActions();
        gui.updateGrid();
    }

    private void turnRight(MouseEvent e){
        gui.getRobot().addBufferedAction(RobotAction.TurnRight);
        gui.getRobot().executeRemainingActions();
        gui.updateGrid();
    }

    private void moveBackward(MouseEvent e){
        gui.getRobot().addBufferedAction(RobotAction.MoveBackward);
        gui.getRobot().executeRemainingActions();
        gui.updateGrid();
    }

    public void importMDF(MouseEvent e) {
        try {
            File myObj = new File("MDF.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                System.out.println(data);
                gui.getMap().getMdfString().setMDFHex(data);
                gui.getMap().updateMapByMDF();
                JOptionPane.showMessageDialog(null,"Import successfully.",
                        "Import",JOptionPane.INFORMATION_MESSAGE);
            }
            myReader.close();
        } catch (FileNotFoundException f) {
            System.out.println("importMDF failed.");
            f.printStackTrace();
        }
    }

    public void exportMDF(MouseEvent e) {
        try {
            FileWriter myWriter = new FileWriter("MDF.txt");
            gui.getMap().updateMDF(); // testing
            myWriter.write(gui.getMap().getMdfString().getMDFHex());
             myWriter.close();
            JOptionPane.showMessageDialog(null,"Export successfully.",
                    "Export",JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException f) {
            System.out.println("exportMDF failed.");
            f.printStackTrace();
        }
    }

    public void getMDFString(MouseEvent e){
        JTextArea textArea = new JTextArea(6, 25);
        textArea.setText(gui.getMap().getMdfString().getMDFHex());
        JOptionPane.showMessageDialog(null, textArea,
                                     "MDF",JOptionPane.INFORMATION_MESSAGE);
    }

    public void runFastestPath(MouseEvent e) {

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

        for(int i = 0; i < gui.getMap().ROW; i++)
            for(int j = 0; j < gui.getMap().COL; j++)
                gui.getMap().getMap()[i][j].setSpecialState(WayPointSpecialState.normal);

        ArrayList<RobotAction> actions = FastestPath.solve(gui.getMap(), robot.getPosition(), gui.getMap().getFPW(),
                                                            robot.getOrientation(), new Orientation(0));
        robot.addBufferedActions(actions);

        actions = FastestPath.solve(gui.getMap(), gui.getMap().getFPW(), gui.getMap().getGoal(),
                new Orientation(0), new Orientation(0));
        robot.addBufferedActions(actions);

        gui.updateGrid();

        fastestPathThread = new Timer();
        fastestPathThread.schedule(new TimerTask() {
            @Override
            public void run() {
                if (robot.gotRemainingActions()) {
                    robot.executeNextAction();
                    gui.updateGrid();
                } else {
                    System.out.println("Path completed.");
                    this.cancel();
                }
            }
        }, executePeriod, executePeriod);
    }

}

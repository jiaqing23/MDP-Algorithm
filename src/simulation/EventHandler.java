package simulation;

import algorithm.FastestPath;
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
import java.util.Scanner;

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
        gui.getMainFrame().getLeftPanel().getGridPanel().updateGrid();
    }

    private void turnLeft(MouseEvent e){
        gui.getRobot().turnLeft();
        gridPanel.updateGrid();
    }

    private void moveForward(MouseEvent e){
        gui.getRobot().moveForward();
        gridPanel.updateGrid();
    }

    private void turnRight(MouseEvent e){
        gui.getRobot().turnRight();
        gridPanel.updateGrid();
    }

    private void moveBackward(MouseEvent e){
        gui.getRobot().moveBackward();
        gridPanel.updateGrid();
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

    public void runFastestPath(MouseEvent e){
        FastestPath.runFastestPath(gui, gui.getRobot(), 100);
    }


}

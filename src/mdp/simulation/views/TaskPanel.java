package mdp.simulation.views;

import javax.swing.*;
import java.awt.*;


public class TaskPanel extends JPanel{
    private JButton fastestPathButton, explorationButton, findImageButton, stopButton;

    public TaskPanel(){
        this.setLayout(new GridLayout(1,3));

        fastestPathButton = new JButton("Fastest Path");
        explorationButton = new JButton("Exploration");
        findImageButton = new JButton("Find Image");
        stopButton = new JButton("Stop");

        Box box = new Box(BoxLayout.X_AXIS);
        box.add(Box.createHorizontalGlue());
        box.add(fastestPathButton);
        box.add(Box.createHorizontalStrut(15));
        box.add(explorationButton);
        box.add(Box.createHorizontalStrut(15));
        box.add(findImageButton);
        box.add(Box.createHorizontalStrut(15));
        box.add(stopButton);
        box.add(Box.createHorizontalGlue());
        this.add(box);

        this.setBackground(new java.awt.Color(48, 95, 114));
    }

    public JButton getExplorationButton() {
        return explorationButton;
    }

    public JButton getFastestPathButton() {
        return fastestPathButton;
    }

    public JButton getFindImageButton() {
        return findImageButton;
    }

    public JButton getStopButton() {
        return stopButton;
    }
}

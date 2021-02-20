package mdp.simulation.views;

import javax.swing.*;
import java.awt.*;


public class TaskPanel extends JPanel{
    private JButton FastestPathButton, ExplorationButton, FindImageButton, StopButton;

    public TaskPanel(){
        this.setLayout(new GridLayout(1,3));

        FastestPathButton = new JButton("Fastest Path");
        ExplorationButton = new JButton("Exploration");
        FindImageButton = new JButton("Find Image");
        StopButton = new JButton("Stop");

        Box box = new Box(BoxLayout.X_AXIS);
        box.add(Box.createHorizontalGlue());
        box.add(FastestPathButton);
        box.add(Box.createHorizontalStrut(15));
        box.add(ExplorationButton);
        box.add(Box.createHorizontalStrut(15));
        box.add(FindImageButton);
        box.add(Box.createHorizontalStrut(15));
        box.add(StopButton);
        box.add(Box.createHorizontalGlue());
        this.add(box);

        this.setBackground(new java.awt.Color(48, 95, 114));
    }

    public JButton getExplorationButton() {
        return ExplorationButton;
    }

    public JButton getFastestPathButton() {
        return FastestPathButton;
    }

    public JButton getFindImageButton() {
        return FindImageButton;
    }

    public JButton getStopButton() {
        return StopButton;
    }
}

package simulation;

import javax.swing.*;
import java.awt.*;

public class MotionPanel extends JPanel {
    private JButton turnLeftButton, turnRightButton, goStraightButton;

    public MotionPanel(){
        this.setLayout(new GridLayout(1,3));

        turnLeftButton = new JButton("Turn left");
        turnRightButton = new JButton("Turn right");
        goStraightButton = new JButton("Go straight");


        Box box = new Box(BoxLayout.X_AXIS);
        box.add(Box.createHorizontalGlue());
        box.add(turnLeftButton);
        box.add(Box.createHorizontalStrut(15));
        box.add(goStraightButton);
        box.add(Box.createHorizontalStrut(15));
        box.add(turnRightButton);
        box.add(Box.createHorizontalGlue());
        this.add(box);

        this.setBackground(new java.awt.Color(48, 95, 114));
    }

    public JButton getTurnLeftButton() {
        return turnLeftButton;
    }

    public JButton getGoStraightButton() {
        return goStraightButton;
    }

    public JButton getTurnRightButton() {
        return turnRightButton;
    }
}

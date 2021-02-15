package simulation.views;

import javax.swing.*;
import java.awt.*;

public class MotionPanel extends JPanel {
    private JButton turnLeftButton, turnRightButton, moveForwardButton, moveBackwardButton;

    public MotionPanel(){
        this.setLayout(new GridLayout(2,1));

        turnLeftButton = new JButton("Turn left");
        turnRightButton = new JButton("Turn right");
        moveForwardButton = new JButton("Move Forward");
        moveBackwardButton = new JButton("Move Backward");


        GridBagLayout layout = new GridBagLayout();

        this.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.ipady = 10;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        this.add(moveForwardButton,gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        this.add(turnLeftButton,gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        this.add(turnRightButton,gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        this.add(moveBackwardButton,gbc);

        this.setBackground(new java.awt.Color(48, 95, 114));
    }

    public JButton getTurnLeftButton() {
        return turnLeftButton;
    }

    public JButton getMoveForwardButton() {
        return moveForwardButton;
    }

    public JButton getTurnRightButton() {
        return turnRightButton;
    }

    public JButton getMoveBackwardButton() {
        return moveBackwardButton;
    }
}

package mdp.simulation.views;

import javax.swing.*;

public class HardwarePanel extends JPanel{

    public HardwarePanel(){
        Box box = new Box(BoxLayout.X_AXIS);
        box.add(Box.createHorizontalGlue());
        box.add(new JButton("Connect to RPi"));
        box.add(Box.createHorizontalStrut(15));
        box.add(new JButton("Test Sensors"));
        box.add(Box.createHorizontalGlue());
        this.add(box);

        this.setBackground(new java.awt.Color(48, 95, 114));
    }
}

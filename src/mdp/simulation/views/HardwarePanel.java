package mdp.simulation.views;

import javax.swing.*;

public class HardwarePanel extends JPanel{
    private JButton connectRPIButton, testSensorButton;

    public HardwarePanel(){
        connectRPIButton = new JButton("Connect to RPi");
        testSensorButton = new JButton("Test Sensors");

        Box box = new Box(BoxLayout.X_AXIS);
        box.add(Box.createHorizontalGlue());
        box.add(connectRPIButton);
        box.add(Box.createHorizontalStrut(15));
        box.add(testSensorButton);
        box.add(Box.createHorizontalGlue());
        this.add(box);

        this.setBackground(new java.awt.Color(48, 95, 114));
    }

    public JButton getConnectRPIButton() {
        return connectRPIButton;
    }

    public JButton getTestSensorButton() {
        return testSensorButton;
    }
}

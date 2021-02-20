package mdp.simulation.views;

import mdp.Main;

import javax.swing.*;
import java.awt.*;


public class ConfPanel extends JPanel{

    private JTextField waypointXTextField, waypointYTextField, coverageLimitTextField, timeLimitTextField,
            simulationSpeedTextField;
    private JCheckBox terminationCheckBox, simulationCheckBox;

    public ConfPanel(){

        waypointXTextField = new JTextField("    1");
        waypointYTextField = new JTextField("    1");
        JPanel panel0 = new JPanel(new FlowLayout());
        panel0.add(new JLabel("Waypoint: X="));
        panel0.add(waypointXTextField);
        panel0.add(new JLabel("Y="));
        panel0.add(waypointYTextField);
        panel0.setBackground(new java.awt.Color(241, 209, 133));

        simulationSpeedTextField = new JTextField("100");
        JPanel panel1 = new JPanel(new FlowLayout());
        panel1.add(new JLabel("Simulation Speed: "));
        panel1.add(simulationSpeedTextField);
        panel1.add(new JLabel("ms/action"));
        panel1.setBackground(new java.awt.Color(241, 209, 133));

        coverageLimitTextField = new JTextField("100");
        timeLimitTextField = new JTextField("100000");
        JPanel panel2 = new JPanel(new FlowLayout());
        panel2.add(new JLabel("Exploration Terminate after: "));
        panel2.add(coverageLimitTextField);
        panel2.add(new JLabel("% or"));
        panel2.add(timeLimitTextField);
        panel2.add(new JLabel("seconds"));
        panel2.setBackground(new java.awt.Color(241, 209, 133));

        JPanel panel3 = new JPanel(new FlowLayout());
        terminationCheckBox = new JCheckBox("Terminate after 1st round");
        panel3.add(terminationCheckBox);
        panel3.setBackground(new java.awt.Color(241, 209, 133));
        terminationCheckBox.setBackground(new java.awt.Color(241, 209, 133));

        JPanel panel4 = new JPanel(new FlowLayout());
        simulationCheckBox = new JCheckBox("Simulation Mode", Main.isSimulating());
        panel4.add(simulationCheckBox);
        panel4.setBackground(new java.awt.Color(241, 209, 133));
        simulationCheckBox.setBackground(new java.awt.Color(241, 209, 133));

        Box box = new Box(BoxLayout.Y_AXIS);
        box.add(Box.createVerticalGlue());
        box.add(panel0);
        box.add(Box.createVerticalStrut(15));
        box.add(panel1);
        box.add(Box.createVerticalStrut(15));
        box.add(panel2);
        box.add(Box.createVerticalStrut(15));
        box.add(panel3);
        box.add(Box.createVerticalStrut(15));
        box.add(panel4);
        box.add(Box.createVerticalGlue());
        this.add(box);

        this.setBorder(BorderFactory.createTitledBorder("Configuration"));

        this.setBackground(new java.awt.Color(241, 209, 133));
    }

    public JCheckBox getSimulationCheckBox() {
        return simulationCheckBox;
    }

    public JCheckBox getTerminationCheckBox() {
        return terminationCheckBox;
    }

    public JTextField getCoverageLimitTextField() {
        return coverageLimitTextField;
    }

    public JTextField getSimulationSpeedTextField() {
        return simulationSpeedTextField;
    }

    public JTextField getTimeLimitTextField() {
        return timeLimitTextField;
    }

    public JTextField getWaypointXTextField() {
        return waypointXTextField;
    }

    public JTextField getWaypointYTextField() {
        return waypointYTextField;
    }
}

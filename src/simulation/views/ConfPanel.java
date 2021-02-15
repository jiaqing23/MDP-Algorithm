package simulation.views;

import javax.swing.*;
import java.awt.*;


public class ConfPanel extends JPanel{
    public ConfPanel(){
        JTextField waypointXTextField = new JTextField("    0");
        JTextField waypointYTextField = new JTextField("    0");
        JPanel panel0 = new JPanel(new FlowLayout());
        panel0.add(new JLabel("Waypoint: X="));
        panel0.add(waypointXTextField);
        panel0.add(new JLabel("Y="));
        panel0.add(waypointYTextField);
        panel0.setBackground(new java.awt.Color(241, 209, 133));

        JTextField coverageLimit = new JTextField("100");
        JTextField timeLimit = new JTextField("100000");
        JPanel panel1 = new JPanel(new FlowLayout());
        panel1.add(new JLabel("Terminate after: "));
        panel1.add(coverageLimit);
        panel1.add(new JLabel("% or"));
        panel1.add(timeLimit);
        panel1.add(new JLabel("seconds"));
        panel1.setBackground(new java.awt.Color(241, 209, 133));

        JTextField simulationSpeed = new JTextField("100");
        JPanel panel2 = new JPanel(new FlowLayout());
        panel2.add(new JLabel("Simulation Speed: "));
        panel2.add(simulationSpeed);
        panel2.add(new JLabel("ms/action"));
        panel2.setBackground(new java.awt.Color(241, 209, 133));

        JPanel panel3 = new JPanel(new FlowLayout());
        JCheckBox terminationCheckBox = new JCheckBox("Terminate after 1st round");
        panel3.add(terminationCheckBox);
        panel3.setBackground(new java.awt.Color(241, 209, 133));
        terminationCheckBox.setBackground(new java.awt.Color(241, 209, 133));


        JPanel panel4 = new JPanel(new FlowLayout());
        JCheckBox simulationCheckBox = new JCheckBox("Simulation Mode");
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
}

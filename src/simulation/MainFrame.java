package simulation;

import java.awt.*;
import javax.swing.JFrame;
import javax.swing.*;


public class MainFrame extends JFrame{

    public MainFrame() {

        this.setTitle("MDP Group 20 Simulator");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(new Dimension((int) (screenSize.getWidth()*0.6), (int) (screenSize.getHeight()*0.8)));
        this.setLayout (new GridBagLayout());
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE );


        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.6;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        this.add(new LeftPanel(), c);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.4;
        c.weighty = 1;
        c.gridx = 1;
        c.gridy = 0;
        this.add(new RightPanel(), c);

        this.getContentPane().setBackground(new java.awt.Color(48, 95, 114));
        this.setVisible(true);
    }
}

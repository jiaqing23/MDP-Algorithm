package simulation;

import javax.swing.*;
import java.awt.*;


public class TaskPanel extends JPanel{

    public TaskPanel(){
        this.setLayout(new GridLayout(1,3));

        Box box = new Box(BoxLayout.X_AXIS);
        box.add(Box.createHorizontalGlue());
        box.add(new JButton("Fastest Path"));
        box.add(Box.createHorizontalStrut(15));
        box.add(new JButton("Exploration"));
        box.add(Box.createHorizontalStrut(15));
        box.add(new JButton("Find Image"));
        box.add(Box.createHorizontalStrut(15));
        box.add(new JButton("Stop"));
        box.add(Box.createHorizontalGlue());
        this.add(box);

        this.setBackground(new java.awt.Color(48, 95, 114));
    }
}

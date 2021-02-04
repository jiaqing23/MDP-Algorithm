package simulation;

import javax.swing.*;
import java.awt.*;

public class RightPanel extends JPanel {

    public RightPanel(){

        Box box = new Box(BoxLayout.Y_AXIS);
        box.add(Box.createVerticalGlue());
        box.add(new MDFPanel());
        box.add(Box.createVerticalStrut(15));
        box.add(new TaskPanel());
        box.add(Box.createVerticalStrut(15));
        box.add(new ConfPanel());
        box.add(Box.createVerticalStrut(15));
        box.add(new HardwarePanel());
        box.add(Box.createVerticalStrut(15));
        box.add(new TimerPanel());
        box.add(Box.createVerticalGlue());
        this.add(box);

        this.setBackground(new java.awt.Color(48, 95, 114));
    }
}

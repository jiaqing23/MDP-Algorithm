package simulation;

import javax.swing.*;
import java.awt.*;

public class MDFPanel  extends JPanel {

    public MDFPanel(){
        this.setLayout (new GridLayout(1,3));

        Box box = new Box(BoxLayout.X_AXIS);
        box.add(Box.createHorizontalGlue());

//        final JButton button = new JButton("Button #" + i);
//        button.setFont(new Font("Calibri", Font.PLAIN, 14));
//        button.setBackground(new Color(0x2dce98));
//        button.setForeground(Color.white);
//        // customize the button with your own look
//        button.setUI(new StyledButtonUI());

        box.add(new JButton("Import"));
        box.add(Box.createHorizontalStrut(15));
        box.add(new JButton("Export"));
        box.add(Box.createHorizontalStrut(15));
        box.add(new JButton("Get String"));
        box.add(Box.createHorizontalGlue());
        this.add(box);

        this.setBackground(new java.awt.Color(48, 95, 114));
    }
}

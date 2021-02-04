package simulation;

import javax.swing.*;
import java.awt.*;

public class GridSquare extends JPanel {

    private int x, y;

    public GridSquare(int x, int y){
        this.x = x;
        this.y = y;

        JLabel label = new JLabel(x + ", " + y);
        label.setForeground(Color.black);
        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 7));
        this.setBackground(Color.white);
        this.add(label);
    }


    public void toggleBackground() {
        if (this.getBackground().equals(Color.black)) {
            this.setBackground(Color.white);
        } else {
            this.setBackground(Color.black);
        }
    }

}

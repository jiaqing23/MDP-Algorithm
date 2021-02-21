package mdp.simulation.views;


import javax.swing.*;
import java.awt.*;

public class TimerPanel extends JPanel{
    private JLabel timerLabel;

    public TimerPanel(){
        timerLabel = new JLabel("00:00");
        timerLabel.setFont(new Font(timerLabel.getFont().getName(), Font.BOLD, 25));

        Box box = new Box(BoxLayout.X_AXIS);
        box.add(Box.createHorizontalGlue());
        box.add(timerLabel);
        box.add(Box.createHorizontalGlue());
        this.add(box);

        this.setBackground(new java.awt.Color(241, 209, 133));
    }

    public JLabel getTimerLabel() {
        return timerLabel;
    }
}

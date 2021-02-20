package mdp.simulation.views;

import javax.swing.*;

public class RightPanel extends JPanel {

    private MDFPanel mdfPanel;
    private TaskPanel taskPanel;
    private ConfPanel confPanel;
    private HardwarePanel hardwarePanel;
    private TimerPanel timerPanel;
    private MotionPanel motionPanel;

    public RightPanel(){
        mdfPanel = new MDFPanel();
        taskPanel = new TaskPanel();
        confPanel = new ConfPanel();
        hardwarePanel = new HardwarePanel();
        timerPanel = new TimerPanel();
        motionPanel = new MotionPanel();

        Box box = new Box(BoxLayout.Y_AXIS);
        box.add(Box.createVerticalGlue());
        box.add(mdfPanel);
        box.add(Box.createVerticalStrut(15));
        box.add(taskPanel);
        box.add(Box.createVerticalStrut(15));
        box.add(confPanel);
        box.add(Box.createVerticalStrut(15));
        box.add(hardwarePanel);
        box.add(Box.createVerticalStrut(15));
        box.add(timerPanel);
        box.add(Box.createVerticalStrut(15));
        box.add(motionPanel);
        box.add(Box.createVerticalGlue());
        this.add(box);

        this.setBackground(new java.awt.Color(48, 95, 114));
    }

    public MDFPanel getMdfPanel() {
        return mdfPanel;
    }

    public ConfPanel getConfPanel() {
        return confPanel;
    }

    public HardwarePanel getHardwarePanel() {
        return hardwarePanel;
    }

    public TaskPanel getTaskPanel() {
        return taskPanel;
    }

    public TimerPanel getTimerPanel() {
        return timerPanel;
    }

    public MotionPanel getMotionPanel() {
        return motionPanel;
    }
}

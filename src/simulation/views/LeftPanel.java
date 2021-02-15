package simulation.views;

import javax.swing.*;

public class LeftPanel extends JPanel {
    private GridPanel gridPanel;

    public LeftPanel(){
        this.gridPanel = new GridPanel();
        this.add(gridPanel);
        this.setBackground(new java.awt.Color(48, 95, 114));
    }

    public GridPanel getGridPanel() {
        return gridPanel;
    }
}

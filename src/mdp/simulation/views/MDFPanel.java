package mdp.simulation.views;

import mdp.map.MDFString;

import javax.swing.*;
import java.awt.*;

public class MDFPanel  extends JPanel {
    private JButton importButton, exportButton, getStringButton, testingButton;

    public MDFPanel(){
        this.setLayout (new GridLayout(1,3));

        importButton=new JButton("Import");
        exportButton=new JButton("Export");
        getStringButton=new JButton("Get String");
        testingButton = new JButton("Update Colour");

        Box box = new Box(BoxLayout.X_AXIS);
        box.add(Box.createHorizontalGlue());

//        final JButton button = new JButton("Button #" + i);
//        button.setFont(new Font("Calibri", Font.PLAIN, 14));
//        button.setBackground(new Color(0x2dce98));
//        button.setForeground(Color.white);
//        // customize the button with your own look
//        button.setUI(new StyledButtonUI());

        box.add(importButton);
        box.add(Box.createHorizontalStrut(15));
        box.add(exportButton);
        box.add(Box.createHorizontalStrut(15));
        box.add(getStringButton);
        box.add(Box.createHorizontalStrut(15));
        box.add(testingButton);
        box.add(Box.createHorizontalGlue());
        this.add(box);

        this.setBackground(new java.awt.Color(48, 95, 114));
    }

    public JButton getExportButton() {
        return exportButton;
    }

    public JButton getImportButton() {
        return importButton;
    }

    public JButton getGetStringButton() {
        return getStringButton;
    }

    public JButton getTestingButton() {
        return testingButton;
    }
}

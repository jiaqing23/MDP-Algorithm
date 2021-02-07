package simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class MDFPanel  extends JPanel {

    public MDFPanel(){
        this.setLayout (new GridLayout(1,3));

        MDFString mdf = new MDFString();
        Box box = new Box(BoxLayout.X_AXIS);
        box.add(Box.createHorizontalGlue());

//        final JButton button = new JButton("Button #" + i);
//        button.setFont(new Font("Calibri", Font.PLAIN, 14));
//        button.setBackground(new Color(0x2dce98));
//        button.setForeground(Color.white);
//        // customize the button with your own look
//        button.setUI(new StyledButtonUI());

        //create button individually
        JButton importBtn=new JButton("Import");
        importBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    File myObj = new File("MDF.txt");
                    Scanner myReader = new Scanner(myObj);
                    while (myReader.hasNextLine()) {
                        String data = myReader.nextLine();
                        System.out.println(data);
                        mdf.setMDFHex(data);
                    }
                    myReader.close();
                } catch (FileNotFoundException f) {
                    System.out.println("An error occurred.");
                    f.printStackTrace();
                }
            }
        });
        box.add(importBtn);
        box.add(Box.createHorizontalStrut(15));

        JButton exportBtn=new JButton("Export");
        exportBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    FileWriter myWriter = new FileWriter("MDF.txt");
                    myWriter.write("FFFFFFFFFFFFFFFFFFFE");
                    myWriter.close();
                    System.out.println("Successfully wrote to the file.");
                } catch (IOException f) {
                    System.out.println("An error occurred.");
                    f.printStackTrace();
                }
            }
        });
        box.add(exportBtn);
        box.add(Box.createHorizontalStrut(15));

        JButton getStrBtn=new JButton("Get String");
        getStrBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            System.out.println(mdf.getMDFHex());
               JOptionPane.showMessageDialog(null,mdf.getMDFHex(),"MDF",JOptionPane.INFORMATION_MESSAGE);
            }
        });
        box.add(getStrBtn);
        box.add(Box.createHorizontalGlue());
        this.add(box);

        this.setBackground(new java.awt.Color(48, 95, 114));
    }


}

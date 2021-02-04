
import javax.swing.*;
import java.io.IOException;

import simulation.GUI;

public class Main {
    private static GUI _gui;

    public static void main(String[] args) throws IOException {
        System.out.println("Initiating GUI...");
        startGUI();
    }

    public static void startGUI() {
        SwingUtilities.invokeLater(() -> {
            _gui = new GUI();
        });
    }
}


import javax.swing.*;
import java.io.IOException;

import map.Map;
import robot.Robot;
import simulation.GUI;

public class Main {
    private static GUI gui;
    private static Robot robot;
    private static Map map;

    public static void main(String[] args) throws IOException {
        System.out.println("Initiating GUI...");
        setup();
    }

    public static void setup() {
        map = new Map();
        robot = new Robot(map);
        SwingUtilities.invokeLater(() -> {
            gui = new GUI(robot, map);
        });
    }
}

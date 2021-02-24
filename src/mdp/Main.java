package mdp;

import mdp.communication.RPIConnection;
import mdp.map.Map;
import mdp.robot.Robot;
import mdp.simulation.GUI;

import javax.swing.*;
import java.io.IOException;

public class Main {
    private static GUI gui;
    private static Robot robot;
    private static Map map;
    private static boolean simulating = true;
    private static RPIConnection rpi;

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

    public static void connectToRpi(){
        rpi = new RPIConnection();
    }

    public static boolean isSimulating() {
        return simulating;
    }

    public static void setSimulating(boolean v) {
        simulating = v;
    }

    public static GUI getGui() {
        return gui;
    }

    public static RPIConnection getRpi() {
        return rpi;
    }
}

package mdp.communication;

import mdp.Main;
import mdp.algorithm.Exploration;
import mdp.robot.Robot;
import mdp.robot.RobotAction;
import mdp.simulation.MouseClickEvent;
import mdp.utils.Orientation;
import mdp.utils.Position;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class RPIConnection {
    private Socket socket;
    private DataInputStream din;
    private DataOutputStream dout;

    private static volatile boolean photoFinishTaken = false;

    private boolean FPCalibration = false;

    public RPIConnection(){
        try {
            System.out.println("Waiting for connection...");
            //socket = new Socket("localhost", 3333);
            socket = new Socket("192.168.20.20", 8080);
            din= new DataInputStream(socket.getInputStream());
            dout= new DataOutputStream(socket.getOutputStream());


            Main.getGui().getMainFrame().getRightPanel().getConfPanel().getSimulationCheckBox().setSelected(false);
            Main.setSimulating(false);

            JOptionPane.showMessageDialog(null,"Connected!",
                    "Connect to RPI",JOptionPane.INFORMATION_MESSAGE);
            System.out.println("Connected to RPI!");

            listen();

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,"Failed. Try Again.",
                    "Connect to RPI",JOptionPane.INFORMATION_MESSAGE);
        }
    }

    void listen(){
        new Thread(() -> {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        String s = receive();
                        System.out.println("Received from RPI: " + s);
                        if (s == null) {
                            try {
                                socket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            this.cancel();
                        } else {
                            String[] commands = s.split("#");
                            for(String command: commands){
                                //System.out.println("Processing command: " + command);
                                processReceive(command);
                            }
                        }
                    }catch (Exception exception){
                        exception.printStackTrace();
                    }
                }
            }, 0, 50);
        }).start();
    }

    public String receive(){
        String s = null;
        byte[] buffer = new byte[256];
        try {
            din.read(buffer);
            s = new String(buffer, 0, buffer.length);
            s = s.trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    public void send(String s){
        try {
            s += "#"; //Termination indicator for RPi
            System.out.println("Sent to RPI: " + s);
            dout.write(s.getBytes());
            dout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processReceive(String s){
        switch (s){
            case "F" -> Main.getGui().trigger(MouseClickEvent.MoveForward);
            case "L" -> Main.getGui().trigger(MouseClickEvent.TurnLeft);
            case "R" -> Main.getGui().trigger(MouseClickEvent.TurnRight);
            case "EX" -> Main.getGui().trigger(MouseClickEvent.RunExploration);
            //TODO: Add calibration command
            case "FP1" -> {
                FPCalibration = true;
                Main.getGui().trigger(MouseClickEvent.RunFastestPath);
                FPCalibration = false;
            }
            case "FP2" -> Main.getGui().trigger(MouseClickEvent.RunFastestPath);
            case "FI" -> Main.getGui().trigger(MouseClickEvent.RunFindImage);
            case "S" -> Main.getGui().trigger(MouseClickEvent.Stop);
            case "D" -> RPIConnection.photoFinishTaken = true;
            default -> {
                if(s.length() == 6){
                    Exploration.setSensingDataFromRPI(s);
                    Robot.setActionCompleted(true);
                }
                else if(s.startsWith("waypoint")){
                    String[] splited = s.split(",");
                    try {
                        Main.getGui().getMainFrame().getRightPanel().getConfPanel().getWaypointXTextField().setText(splited[1]);
                        Main.getGui().getMainFrame().getRightPanel().getConfPanel().getWaypointYTextField().setText(splited[2]);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void sendPathCommand(ArrayList<RobotAction> actions){
        if(actions.size() == 0) return;

        String s = "";
        for(RobotAction action: actions){
            switch (action){
                case MoveForward -> s += "W";
                case TurnLeft -> s += "A";
                case TurnRight -> s += "D";
            }
        }
        int count = 0;
        String s2 = "AL|AR|";
        if(FPCalibration) s2 += "QQ";
        for(int i = 0; i < s.length(); i++){
            //if(i == 0 || s.charAt(i)!=s.charAt(i-1) || s.charAt(i-1) != 'W'){
                if(i != 0) s2 += (char)((int)'0' + count);
                s2 += s.charAt(i);
                count = 0;
            //}
            count++;
        }
        if(count > 0) s2 += (char)((int)'0' + count); //count = 0 means s = ""

//        System.out.println(s);
//        System.out.println(s2);
        send(s2);
    }


    public void sendTakePhotoCommand(Position position, Orientation cameraOrientation, int dl, int dm, int dr){
        String x = String.valueOf(position.x());
        String y = String.valueOf(position.y());
        String o = String.valueOf(cameraOrientation.getOrientation());
        String dll = String.valueOf(dl);
        String dmm = String.valueOf(dm);
        String drr = String.valueOf(dr);
        System.out.println("Take Photo");
        photoFinishTaken = false;
        send("AL|RP|P,"+y+","+x+","+o+","+dll+","+dmm+","+drr);
        while(!photoFinishTaken) { //Wait a while and check
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMDFString(){
        Main.getGui().getMap().updateMDF();
        send("AL|AN|MDF,"+Main.getGui().getMap().getMdfString().getMDFHex1()+","+Main.getGui().getMap().getMdfString().getMDFHex2());
    }
}

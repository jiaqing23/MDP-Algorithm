package mdp.communication;

import mdp.Main;
import mdp.algorithm.Exploration;
import mdp.robot.Robot;
import mdp.robot.RobotAction;
import mdp.simulation.MouseClickEvent;

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

    private boolean TESTING = true;
    private String IPAddress = TESTING?"localhost":"192.168.20.20";
    private int port = true?3333:8000;

    public RPIConnection(){
        try {
            System.out.println("Waiting for connection...");
            socket = new Socket(IPAddress, port);
            //socket.bind(new java.net.InetSocketAddress("192.168.20.20", 8080));
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
                            processReceive(s);
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
            case "E" -> Main.getGui().trigger(MouseClickEvent.RunExploration);
            case "P" -> Main.getGui().trigger(MouseClickEvent.RunFastestPath);
            case "A" -> Main.getGui().trigger(MouseClickEvent.RunFindImage);
            case "S" -> Main.getGui().trigger(MouseClickEvent.Stop);
            default -> {
                if(s.length() == 6){
                    Exploration.setSensingDataFromRPI(s);
                    Robot.setActionCompleted(true);
                }
            }
        }
    }

    public void sendPathCommand(ArrayList<RobotAction> actions){
        String s = "";
        for(RobotAction action: actions){
            switch (action){
                case MoveForward -> s += "W";
                case TurnLeft -> s += "A";
                case TurnRight -> s += "D";
            }
        }
        int count = 0;
        String s2 = "";
        for(int i = 0; i < s.length(); i++){
            if(i == 0 || s.charAt(i)!=s.charAt(i-1)){
                if(i!=0) s2 += (char)((int)'0' + count);
                s2+=s.charAt(i);
                count = 0;
            }
            count++;
        }
        if(count > 0) s2 += (char)((int)'0' + count); //count = 0 means s = ""

        System.out.println(s);
        System.out.println(s2);
        send(s2);
    }

    //TODO
    public void sendTakePhotoCommand(){
        //WAIT RECEIVE
        System.out.println("Take Photo");
    }
}

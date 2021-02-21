package mdp.communication;

import mdp.Main;
import mdp.simulation.MouseClickEvent;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class RPIConnection {
    private Socket socket;
    private DataInputStream din;
    private DataOutputStream dout;

    public RPIConnection(){
        try {
            System.out.println("Waiting for connection...");
            socket = new Socket("localhost",3333);
            din= new DataInputStream(socket.getInputStream());
            dout= new DataOutputStream(socket.getOutputStream());

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
                    String s = receive();
                    if(s == null){
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        this.cancel();
                    }
                    else{
                        processCommand(s);
                    }
                    System.out.println("Received from RPI: " + s);
                }
            }, 0, 50);
        }).start();
    }

    public String receive(){
        String s = null;
        try {
            s = din.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    public void send(String s){
        try {
            dout.writeUTF(s);
            dout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processCommand(String s){
        switch (s){
            case "F" -> Main.getGui().trigger(MouseClickEvent.MoveForward);
            case "L" -> Main.getGui().trigger(MouseClickEvent.TurnLeft);
            case "R" -> Main.getGui().trigger(MouseClickEvent.TurnRight);
            case "E" -> Main.getGui().trigger(MouseClickEvent.RunExploration);
            case "P" -> Main.getGui().trigger(MouseClickEvent.RunFastestPath);
        }
    }
}

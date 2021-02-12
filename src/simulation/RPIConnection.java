package simulation;

import java.net.*;
import java.io.*;

public class RPIConnection {
    DataInputStream din;
    DataOutputStream dout;
    String incomingData ="";
    int port = 3333;

    RPIConnection() throws IOException {
        ServerSocket ss=new ServerSocket(port);
        Socket s=ss.accept();
        din=new DataInputStream(s.getInputStream());
        dout=new DataOutputStream(s.getOutputStream());
    }

    public void sendData(String data) throws IOException {
        dout.writeUTF(data);
        dout.flush();
    }

    public  void receiveData() throws IOException {
        incomingData = din.readUTF();
    }
}

package mdp.communication;

import java.net.*;
import java.io.*;
import java.util.Timer;
import java.util.TimerTask;

public class Client{
    public static void main(String args[])throws Exception{
        Socket s=new Socket("localhost",3333);
        DataInputStream din=new DataInputStream(s.getInputStream());
        DataOutputStream dout=new DataOutputStream(s.getOutputStream());
        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));

        new Thread() {
            @Override
            public void run() {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        String s= null;
                        try {
                            s = din.readUTF();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("server says: "+s);
                    }
                }, 0, 50);
            };
        }.start();

        String str="",str2="";
        while(!str.equals("stop")){
            str=br.readLine();
            dout.writeUTF(str);
            dout.flush();
        }

        dout.close();
        s.close();
    }


}
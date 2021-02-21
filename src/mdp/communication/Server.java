package mdp.communication;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class Server {
    private static DataInputStream din;
    private static DataOutputStream dout;
    private static BufferedReader br;

    public static void main(String args[]) throws Exception {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(3333);
            Socket s = ss.accept();

            din = new DataInputStream(s.getInputStream());
            dout = new DataOutputStream(s.getOutputStream());
            br = new BufferedReader(new InputStreamReader(System.in));

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
                            System.out.println("client says: "+s);
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    void listen(){
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
                        System.out.println("client says: "+s);
                    }
                }, 0, 50);
            };
        }.start();
    }

    void send(String s){
        try {
            dout.writeUTF(s);
            dout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
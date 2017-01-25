package girondins.locations;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Girondins on 05/10/15.
 */
public class TCPConnect extends Thread{
    private ExecuteThread thread;
    private Buffer<JSONObject> receiverBuff;
    private Receive receiver;
    private String ip;
    private int port;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private InetAddress address;



    public  TCPConnect(String ip, int port) {

        this.ip = ip;
        this.port = port;
        System.out.println(ip +" " + port);
        thread = new ExecuteThread();
        thread.start();
        receiverBuff = new Buffer<JSONObject>();

    }


    public class getServ extends Binder{
        public TCPConnect getService(){
            return TCPConnect.this;
        }
    }

    public void enableConnect(){
        thread.execute(new Connect());
    }

    public void disconnect(){
        thread.execute(new Disconnect());
    }


    public void task(JSONObject response){
        thread.execute(new sendTask(response));
    }

    public void task(JSONArray responseArray){
        thread.execute(new sendTask(responseArray));
    }

    public JSONObject response() throws InterruptedException {
        return receiverBuff.get();
    }

    private class Connect implements Runnable {
        @Override
        public void run() {
            try {
                address = InetAddress.getByName(ip);
                socket = new Socket(address, port);
                Log.d("Connct", address.toString());
                Log.d("reciver.start", "start");
                dos = new DataOutputStream(socket.getOutputStream());
                dos.flush();
                Log.d("reciver.start", "start");
                dis = new DataInputStream(socket.getInputStream());
                receiver = new Receive();
                receiver.start();
                Log.d("reciver.start", "start");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private class Disconnect implements Runnable {
        public void run() {
            try {
                if (dis != null)
                    dis.close();
                if (dos != null)
                    dos.close();
                if (socket != null)
                    socket.close();
                thread.stop();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class sendTask implements Runnable{
        private JSONObject response;
        private JSONArray responseArray;

        public sendTask(JSONObject response){
            this.response = response;
        }

        public sendTask(JSONArray responseArray){
            this.responseArray = responseArray;
        }
        @Override
        public void run() {
            try{
                if(response!=null){
                    dos.writeUTF(response.toString());
                    Log.d("SENDTASK", response.toString());
                    dos.flush();
                }
//                if(receiver == null){
//                    receiver = new Receive();
//                    receiver.start();
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class Receive extends Thread {
        public void run() {
            String information;
            try {
                while (receiver != null) {
                    information = dis.readUTF();
                    Log.d("TPCCONRESPONSE", information);
                    JSONObject jsonInfo = new JSONObject(information);
                    receiverBuff.put(jsonInfo);
                }
            } catch (Exception e) {
                e.printStackTrace();
                receiver = null;
            }
        }
    }


}

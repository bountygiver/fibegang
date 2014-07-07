package com.example.fibe.studentfibeclient;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class ClientActivity extends Activity {

    private EditText username=null;
    private EditText  password=null, ipText=null;
    private TextView attempts;
    private Button login;
    private InetAddress serverAddr;
//    private Button signup;

    private static final int SERVERPORT = 56789;

//    private Socket socket;
    private DatagramSocket clientSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText)findViewById(R.id.editText1);
        password = (EditText)findViewById(R.id.editText2);
        ipText = (EditText) findViewById(R.id.ip_field);

        login = (Button)findViewById(R.id.button1);
//        signup = (Button)findViewById(R.id.button2);
        login.setOnClickListener(loginConnectOnClickListener);
    }



    View.OnClickListener loginConnectOnClickListener =
            new View.OnClickListener(){

                @Override
                public void onClick(View arg0) {
                    //new Thread(new ClientThread(ipText.getText().toString())).start();

                    try {
                        serverAddr = InetAddress.getByName(ipText.getText().toString());
                        clientSocket = new DatagramSocket();
                        byte[] buf = createJSON().toString().getBytes();
                        DatagramPacket dp = new DatagramPacket(buf, buf.length, serverAddr, SERVERPORT);
                        new Thread(new ClientThread(clientSocket, dp)).start();

                        //out.println("SENT!");
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

    public JSONObject createJSON () {
        JSONObject object = new JSONObject();
        try {
            JSONArray path = new JSONArray();
            object.put("sessionid", 0);
            object.put("path", path);
            object.put("request", "login");
            object.put("identity", 0);
            object.put("sessionkey", "");
            JSONObject payload = new JSONObject();
            payload.put("username", username.getText().toString());
            payload.put("password", password.getText().toString());
            payload.put("sessionkey", "");
            object.put("payload", payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public void onConnect(View view) {

        EditText it = (EditText) findViewById(R.id.ip_field);
        //new Thread(new ClientThread(it.getText().toString())).start();
    }

    class ClientThread implements Runnable {
        DatagramSocket sock;
        DatagramPacket packet;

        public ClientThread(DatagramSocket sock, DatagramPacket packet) {
            this.sock = sock;
            this.packet = packet;
        }

        @Override
        public void run() {

            try {
                sock.send(packet);

                byte[] buf = new byte[4096];
                DatagramPacket dp = new DatagramPacket(buf, 0);
                sock.receive(dp);
                int len = sock.getReceiveBufferSize();
                String s = String.valueOf(buf);
                try {
                    JSONObject payload = new JSONObject(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                clientSocket.disconnect();
                clientSocket.close();


            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.client, menu);
        return true;
    }

}
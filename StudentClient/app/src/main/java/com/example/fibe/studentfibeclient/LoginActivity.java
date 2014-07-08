package com.example.fibe.studentfibeclient;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import android.content.Intent;


public class LoginActivity extends Activity {

    private EditText username=null;
    private EditText  password=null, ipText=null;
    private Button login;
//    private Button signup;
    private InetAddress serverAddr;

    private static final int SERVERPORT = 56789;

    private DatagramSocket clientSocket;

    public static LoginActivity thisActivity;
    public JSONObject payload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText)findViewById(R.id.username_field);
        password = (EditText)findViewById(R.id.password_field);
        ipText = (EditText) findViewById(R.id.ip_field);

        login = (Button)findViewById(R.id.loginButton);
//        signup = (Button)findViewById(R.id.button2);
        login.setOnClickListener(loginConnectOnClickListener);
        thisActivity = this;
    }



    View.OnClickListener loginConnectOnClickListener =
            new View.OnClickListener(){

                @Override
                public void onClick(View arg0) {
                    //new Thread(new ClientThread(ipText.getText().toString())).start();

                    try {
                        serverAddr = InetAddress.getByName(ipText.getText().toString());
                        clientSocket = new DatagramSocket();
                        byte[] buf = createLoginJSON().toString().getBytes();
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


    public void Signup (View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        intent.putExtra("ip_server", serverAddr);
        startActivity(intent);
    }

    public JSONObject createLoginJSON() {
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
                DatagramPacket dp = new DatagramPacket(buf, 4096);
                sock.receive(dp);
                buf = dp.getData();
                int len = sock.getReceiveBufferSize();
                final String payload_string = new String(buf);
                try {
                    payload = new JSONObject(payload_string);

                    if (payload.getString("status").equals("success")) {
                        Intent new_intent = new Intent(thisActivity, PickclassActivity.class);
                        new_intent.putExtra("payload", payload_string);
                        new_intent.putExtra("ip_server", serverAddr);
                        thisActivity.startActivity(new_intent);
                        thisActivity.finish();
                    } else {
                        // Give error message
                    }
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
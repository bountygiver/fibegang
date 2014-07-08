package com.example.fibe.studentfibeclient;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import java.net.UnknownHostException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.view.View;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SignupActivity extends Activity {

    private EditText username=null;
    private EditText  password=null;
//    private Button confirm_button;
    private InetAddress serverAddress;
    private static final int SERVERPORT = 56789;

    private DatagramSocket clientSocket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        serverAddress = (InetAddress) getIntent().getSerializableExtra("ip_server");
        username = (EditText)findViewById(R.id.username_field);
        password = (EditText)findViewById(R.id.password_field);
//        confirm_button = (Button)findViewById(R.id.confirm_button);
    }

    public void Confirm (View view) {
        try {
            clientSocket = new DatagramSocket();
            byte[] buf = createSignupJSON().toString().getBytes();
            DatagramPacket dp = new DatagramPacket(buf, buf.length, serverAddress, SERVERPORT);
            new Thread(new ClientThread(clientSocket, dp)).start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public JSONObject createSignupJSON() {
        JSONObject object = new JSONObject();
        try {
            JSONArray path = new JSONArray();
            object.put("sessionid", 0);
            object.put("path", path);
            object.put("request", "regist");
            object.put("identity", 0);
            object.put("sessionkey", "");
            JSONObject payload = new JSONObject();
            payload.put("username", username.getText().toString());
            payload.put("password", password.getText().toString());
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.signup, menu);
        return true;
    }
}

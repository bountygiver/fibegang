package com.example.fibe.client;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import org.json.*;

public class ClientActivity extends Activity {

    private Socket socket;

    private static final int SERVERPORT = 56789;
//    private static final String SERVER_IP = "10.0.2.2";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

    }

    public void onClick(View view) {
        try {
            EditText et = (EditText) findViewById(R.id.text_field);
            String str = et.getText().toString();
            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())),
                    true);
            out.println(str);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
            payload.put("username", username);
            payload.put("password", password);
            payload.put("password", "");
            object.put("payload", payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }


    public void onConnect(View view) {

        EditText it = (EditText) findViewById(R.id.ip_field);
        new Thread(new ClientThread(it.getText().toString())).start();
    }

    class ClientThread implements Runnable {
        String ip;

        public ClientThread(String ip) {
            this.ip = ip;
        }

        @Override
        public void run() {

            try {
                InetAddress serverAddr = InetAddress.getByName(ip);

                socket = new Socket(serverAddr, SERVERPORT);

            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }

    }
}
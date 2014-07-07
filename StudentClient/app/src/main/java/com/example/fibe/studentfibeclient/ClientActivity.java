package com.example.fibe.studentfibeclient;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
//    private Button signup;

    private static final int SERVERPORT = 56789;

    private Socket socket;

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
                    new Thread(new ClientThread(ipText.getText().toString())).start();

                    try {
                        PrintWriter out = new PrintWriter(new BufferedWriter(
                                new OutputStreamWriter(socket.getOutputStream())),
                                true);
                        out.println("Begin packet transfer");
                        out.println(createJSON().toString());
                        out.println("End of packet transfer");

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

                socket = new Socket(ip, SERVERPORT);

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
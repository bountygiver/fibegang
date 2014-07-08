package com.example.fibe.studentfibeclient;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.fibe.studentfibeclient.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PickclassActivity extends Activity {

    int sessionid;
    InetAddress serverAddress;
    int serverport = 56789;
    PickclassActivity thisActivty;
    ListView cList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickclass);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        cList = (ListView) findViewById(R.id.classList);

        Intent thisIntent = this.getIntent();
        thisActivty = this;
        String payload_string = thisIntent.getStringExtra("payload");
        serverAddress = (InetAddress) getIntent().getSerializableExtra("ip_server");
        try {
            JSONObject payload = new JSONObject(payload_string);
            sessionid = payload.getJSONObject("payload").getInt("sessionid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JSONObject classRequest = createClassJSON();
        Thread getClass = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramSocket s = new DatagramSocket();
                    byte[] buf = classRequest.toString().getBytes();
                    DatagramPacket dp = new DatagramPacket(buf, buf.length, serverAddress, serverport);
                    s.send(dp);
                    s.receive(dp);
                    int len = s.getReceiveBufferSize();
                    String payload_string = String.valueOf(buf);

                    final JSONObject response = new JSONObject(payload_string);
                    thisActivty.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            List<ClassItem> classList = new ArrayList<ClassItem>();
                            try {
                                Iterator<String> keyIterator = response.getJSONObject("payload").keys();
                                while (keyIterator.hasNext()) {
                                    String k = keyIterator.next();
                                    ClassItem s = new ClassItem(k, response.getJSONObject("payload").getString(k));
                                    classList.add(s);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String[] s = new String[classList.size()];
                            int[] views = new int[] {android.R.id.text1};
                            classList.toArray(s);
                            ListAdapter adapter = new ArrayAdapter(thisActivty, android.R.layout.simple_list_item_1, s);
                            cList.setAdapter(adapter);
                            cList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                }
                            });
                        }
                    });

                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public static class ClassItem {
        String path, desc;

        public ClassItem(String path, String desc) {
            this.path = path;
            this.desc = desc;
        }

        @Override
        public String toString() {
            return path + " - " + desc;
        }
    }

    public JSONObject createClassJSON() {
        JSONObject object = new JSONObject();
        try {
            JSONArray path = new JSONArray();
            object.put("sessionid", sessionid);
            object.put("path", path);
            object.put("request", "classList");
            object.put("identity", 0);
            object.put("sessionkey", "");
            JSONObject payload = new JSONObject();
            object.put("payload", payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pickclass, menu);
        return true;
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_pickclass, container, false);
            return rootView;
        }
    }
}

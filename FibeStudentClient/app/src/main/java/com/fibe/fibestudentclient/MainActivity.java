package com.fibe.fibestudentclient;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import BackEnd.ClientEngine;


public class MainActivity extends Activity {

    public static ClientEngine client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new ClientEngine();
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class MainFragment extends Fragment {

        public EditText addressBox;
        public Button connectButton;
        public ProgressBar pBar;

        public MainFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            addressBox = (EditText) rootView.findViewById(R.id.txtAddress);
            connectButton = (Button) rootView.findViewById(R.id.btnConnect);
            pBar = (ProgressBar) rootView.findViewById(R.id.connectProgressBar);

            connectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    connectButton.setEnabled(false);
                    pBar.setVisibility(View.VISIBLE);
                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            boolean x = MainActivity.client.ConnectTo(addressBox.getText().toString());
                            if (x) {
                                //Proceed

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent in = new Intent(getActivity(), LoginActivity.class);
                                        startActivity(in);
                                        getActivity().finish();
                                    }
                                });
                            } else {
                                //Fail
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog dg = new AlertDialog.Builder(getActivity()).create();
                                        dg.setTitle("Error");
                                        dg.setMessage(MainActivity.client.getError());
                                        dg.setButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                }
                                            });

                                        dg.show();
                                        connectButton.setEnabled(true);
                                        pBar.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }
                        }
                    };
                    new Thread(r).start();
                }
            });
            return rootView;
        }
    }
}

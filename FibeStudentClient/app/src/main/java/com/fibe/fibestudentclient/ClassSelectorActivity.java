package com.fibe.fibestudentclient;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import BackEnd.RoomItem;

public class ClassSelectorActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_selector);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new ClassSelectorFragment(), "class_list_tag")
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.class_selector, menu);
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
        } else if (id == R.id.class_selector_refresh) {
            ClassSelectorFragment pf = (ClassSelectorFragment) getFragmentManager()
                    .findFragmentByTag("class_list_tag");
            pf.refreshList();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ClassSelectorFragment extends Fragment {

        public ListView classList;
        public ArrayAdapter<RoomItem> classListAdapter;
        private RefreshListTask mRefresfTask;

        public ClassSelectorFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_class_selector, container, false);
            classList = (ListView) rootView.findViewById(R.id.listClass);
            refreshList();
            return rootView;
        }

        public void refreshList() {
            if (mRefresfTask != null) return;
            mRefresfTask = new RefreshListTask();
            mRefresfTask.execute();
            Toast.makeText(getActivity(), "Retrieving class list...", Toast.LENGTH_SHORT).show();

        }

        public class RefreshListTask extends AsyncTask<Void, Void, Boolean> {
            @Override
            protected Boolean doInBackground(Void... voids) {
                boolean x = MainActivity.client.list();
                return x;
            }

            @Override
            protected void onPostExecute(final Boolean success) {
                mRefresfTask = null;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (success) {
                                List<RoomItem> fromColumns = MainActivity.client.getRoomAvailable();
                                classListAdapter = new ArrayAdapter<RoomItem>(getActivity()
                                        , R.layout.class_item, fromColumns);
                                classList.setAdapter(classListAdapter);
                                // Create a message handling object as an anonymous class.
                                AdapterView.OnItemClickListener mMessageClickedHandler =
                                        new AdapterView.OnItemClickListener() {
                                    public void onItemClick(AdapterView parent, View v, int position
                                            , long id) {
                                        // Do something in response to the click

                                        Intent i = new Intent(getActivity(), RoomActivity.class);
                                        i.putExtra("room"
                                                , (RoomItem) parent.getItemAtPosition(position));
                                        startActivity(i);
                                    }
                                };

                                classList.setOnItemClickListener(mMessageClickedHandler);
                            } else {
                                Toast.makeText(getActivity(), "Failed to retrieve list of classes"
                                        , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            }

            @Override
            protected void onCancelled() {
                mRefresfTask = null;
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Refresh canceled!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }
}

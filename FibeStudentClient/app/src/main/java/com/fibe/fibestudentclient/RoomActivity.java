package com.fibe.fibestudentclient;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import BackEnd.Room;
import BackEnd.RoomItem;

public class RoomActivity extends Activity implements TagsFragment.OnFragmentInteractionListener {

    public Room currentRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        Intent thisIntent = getIntent();
        RoomItem ri = (RoomItem) thisIntent.getSerializableExtra("room");
        currentRoom = MainActivity.client.JoinRoom(ri);
        this.setTitle(currentRoom.getTitle());
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.roomTagFrame, new TagsFragment())
                    .commit();
        }
    }

    @Override
    public void onFragmentInteraction(String id) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class RoomFragment extends Fragment {

        public Button btn_enqueue;

        public RoomFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_room, container, false);
            getFragmentManager().beginTransaction().add(R.id.roomListFrame, new TagsFragment()).commit();
            return rootView;
        }
    }
}

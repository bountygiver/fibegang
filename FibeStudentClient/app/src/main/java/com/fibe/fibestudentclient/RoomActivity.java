package com.fibe.fibestudentclient;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.fibe.fibestudentclient.Items.Tags;

import java.util.Observable;
import java.util.Observer;

import BackEnd.Room;
import BackEnd.RoomItem;
import BackEnd.TalkSession;

public class RoomActivity extends Activity implements TagsFragment.OnFragmentInteractionListener, TalkFragment.OnFragmentInteractionListener {

    public Room currentRoom;

    public RoomFragment mRoomFragment;
    public TalkFragment mTalkFragment;
    public RoomObserver mRoomObserver;

    public Button mBtnPing;

    public void switchFragment(Fragment f) {

        FragmentTransaction t = getFragmentManager().beginTransaction();
        t.replace(R.id.roomTagFrame, f);
        t.addToBackStack(null);
        t.commit();
    }

    public void MakeTalkFragment() {
        //getFragmentManager().beginTransaction().add(new TalkFragment(), "ROOM_TALK").commit();
        mTalkFragment = (TalkFragment) getFragmentManager().findFragmentByTag("ROOM_TALK");
    }

    public void onSendRequest() {
        currentRoom.addPing(null);
    }

    public void BackToRoom() {
        TagsFragment f = new TagsFragment();
        FragmentTransaction t = getFragmentManager().beginTransaction();
        getFragmentManager().popBackStackImmediate();
        t.replace(R.id.roomTagFrame, f);
        t.commit();
        mBtnPing.setVisibility(View.VISIBLE);
    }

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
                    .add(R.id.roomTagFrame, new TagsFragment(), "ROOM_TAG")
                    .commit();
        }
        MakeTalkFragment();

        mRoomObserver = new RoomObserver();
        currentRoom.addObserver(mRoomObserver);
        mBtnPing = (Button) findViewById(R.id.btnEnqueue);
        mBtnPing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTalkFragment = TalkFragment.newInstance("", "");
                currentRoom.addPing(Tags.getSelectedTagArray());
                mBtnPing.setVisibility(View.GONE);
                switchFragment(mTalkFragment);
            }
        });
    }

    @Override
    public void onFragmentInteraction(String id) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

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

    private class RoomObserver implements Observer {
        @Override
        public void update(Observable observable, Object o) {
            if (observable.getClass() == Room.class) {
                if (o.toString().equals("SESSION_CREATED")) {
                    Room r = (Room) observable;
                    mTalkFragment.assignTalkSession(r.getCurrentAsk());
                }
            } else if (observable.getClass() == TalkSession.class) {
                if (o != null) {
                }
            }
        }
    }
}

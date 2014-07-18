package com.fibe.fibestudentclient;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fibe.fibestudentclient.Items.Tags;

import java.util.Observable;
import java.util.Observer;

import BackEnd.TalkSession;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TalkFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TalkFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class TalkFragment extends Fragment implements Observer {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Button mCancelBtn, mCancelRequestBtn;
    public TextView mTxtClassName, mTxtTags, mTalkDest;
    public TalkSession mTalkSession;
    public LinearLayout mTalkFrame, mReqFrame;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TalkFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TalkFragment newInstance(String param1, String param2) {
        TalkFragment fragment = new TalkFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void assignTalkSession(TalkSession ts) {
        try {
            mTalkSession = ts;
            ts.addObserver(this);
            mTxtTags.setText(ts.getTagsString());
            mTxtClassName.setText("Requesting: " + ts.getRoomName());
        } catch (Exception ex) {

        }
    }

    public TalkFragment() {
        // Required empty public constructor
    }

    public void LinkTalkSession(TalkSession ts) {
        mTalkSession = ts;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_talk, container, false);

        mCancelBtn = (Button) view.findViewById(R.id.btnStopTalking);
        mCancelRequestBtn = (Button) view.findViewById(R.id.btnCancelRequest);
        mTxtClassName = (TextView) view.findViewById(R.id.classTalkSessionText);
        mTxtTags = (TextView) view.findViewById(R.id.tagText);
        mTalkDest = (TextView) view.findViewById(R.id.txtTalkingDest);
        mReqFrame = (LinearLayout) view.findViewById(R.id.frameRequesting);
        mTalkFrame = (LinearLayout) view.findViewById(R.id.frameTalking);

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTalkSession.CancelRequest();
                ((RoomActivity) getActivity()).BackToRoom();
            }
        });

        mCancelRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTalkSession.CancelRequest();
                ((RoomActivity) getActivity()).BackToRoom();
            }
        });

        if (mTalkSession != null) {
            mTxtTags.setText(mTalkSession.getTagsString());
            mTxtClassName.setText("Requesting: " + mTalkSession.getRoomName());
        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable != mTalkSession) return;
        final Object x = o;
        final Observable obs = observable;
        final TalkFragment thisFrag = this;
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (x.equals("START_TALKING")) {
                        mReqFrame.setVisibility(View.GONE);
                        mTalkFrame.setVisibility(View.VISIBLE);
                        mTalkDest.setText("Talking: " + mTalkSession.getRoomName());
                    } else if (x.equals("CANCELED")) {
                        obs.deleteObserver(thisFrag);
                        ((RoomActivity) thisFrag.getActivity()).BackToRoom();
                    }
                }
            });
        } catch (Exception ex) {

        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


}

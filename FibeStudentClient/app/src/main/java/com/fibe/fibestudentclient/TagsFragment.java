package com.fibe.fibestudentclient;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.TextView;


import com.fibe.fibestudentclient.Items.Tags;

import java.io.Serializable;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
@SuppressWarnings("JavadocReference")
public class TagsFragment extends Fragment implements AbsListView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private TagAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static TagsFragment newInstance(String param1, String param2) {
        TagsFragment fragment = new TagsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TagsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // TODO: Change Adapter to display your content
        mAdapter = new TagAdapter(getActivity(), Tags.ITEMS);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        boolean[] ba = new boolean[mAdapter.getChecked().size()];
        for(int i = 0; i < ba.length; i++) {
            ba[i] = mAdapter.getChecked().get(i);
        }
        outState.putBooleanArray("Checked", ba);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tags, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        if (savedInstanceState != null) {
            mAdapter.loadChecked(savedInstanceState.getBooleanArray("Checked"));
        }
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(Tags.ITEMS.get(position).id);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyText instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
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
        public void onFragmentInteraction(String id);
    }



    private class TagAdapter extends ArrayAdapter<Tags.TagItem> implements CompoundButton.OnCheckedChangeListener, Serializable {

        Context context;
        int layoutResourceId;
        Tags.TagItem data[] = null;
        CheckBox[] cBox;

        SparseBooleanArray mCheckStates;

        public SparseBooleanArray getChecked() {
            return mCheckStates;
        }

        public void loadChecked(boolean[] ba) {
            for(int i = 0; i < ba.length; i++) {
                mCheckStates.put(i, ba[i]);
                data[i].isSelected = ba[i];
            }
        }

        public TagAdapter(Context context, List<Tags.TagItem> objects) {
            super(context, R.layout.tag_item, objects);
            this.context = context;
            this.layoutResourceId = R.layout.tag_item;
            this.data = new Tags.TagItem[objects.size()];
            objects.toArray(this.data);
            cBox = new CheckBox[objects.size()];
            mCheckStates = new SparseBooleanArray(data.length);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){

            View row = convertView; row = null;
            CheckBox chkSelect;

            if (row == null){

                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                chkSelect = (CheckBox) row.findViewById(R.id.txtToggleTag);

            }
            else{
                chkSelect = (CheckBox) row.findViewById(R.id.txtToggleTag);
            }


            Tags.TagItem tag = data[position];
            chkSelect.setText(tag.toString());
            chkSelect.setTag(position);
            chkSelect.setChecked(tag.isSelected);
            chkSelect.setOnCheckedChangeListener(this);
            cBox[position] = chkSelect;
            return row;

        }

        public boolean isChecked(int position) {
            return mCheckStates.get(position, false);
        }
/*
        public void setChecked(int position, boolean isChecked) {
            mCheckStates.put(position, isChecked);
            if (cBox[position] != null) cBox[position].setChecked(isChecked);
        }

        public void toggle(int position) {
            setChecked(position, !isChecked(position));

        }*/
        @Override
        public void onCheckedChanged(CompoundButton chkView,
                                     boolean isChecked) {
            mCheckStates.put((Integer) chkView.getTag(), isChecked);
            data[(Integer) chkView.getTag()].isSelected = isChecked;

        }
    }

}

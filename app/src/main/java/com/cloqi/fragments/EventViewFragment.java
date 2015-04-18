package com.cloqi.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.cloqi.R;
import com.cloqi.app.AppConfig;
import com.cloqi.gui.EventAdapter;
import com.cloqi.youpayframework.YouPayEvent;

/**
 *
 * Created by Lunding on 07/02/15.
 */
public class EventViewFragment extends Fragment {

    //Constants
    private static final String TAG = EventViewFragment.class.getSimpleName();

    //Fields
    EventAdapter adapter;

    public EventViewFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Event view Fragment initializing...");
        View rootView = inflater.inflate(R.layout.fragment_event_gridview, container, false);

        GridView gridview = (GridView) rootView.getRootView().findViewById(R.id.gridview_event);
        adapter = new EventAdapter(getActivity());
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT).show();
                YouPayEvent event = (YouPayEvent) adapter.getItem(position);
                EventEditFragment fragment = EventEditFragment.newInstance(event.getDBid());
                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, fragment).addToBackStack("")
                        .commit();
            }
        });
        Log.d(TAG, "Event view Fragment initialized");
        return rootView;
    }

}

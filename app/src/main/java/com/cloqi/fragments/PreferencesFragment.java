package com.cloqi.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cloqi.R;

/**
 *
 * Created by Lunding on 18/04/15.
 */
public class PreferencesFragment extends Fragment {

    //Constants
    public static final String TAG = PreferencesFragment.class.getSimpleName();

    //Fields

    public PreferencesFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_preferences, container, false);

        return rootView;
    }
}

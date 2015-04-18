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
public class AboutFragment extends Fragment {

    //Constants
    public static final String TAG = AboutFragment.class.getSimpleName();

    //Fields

    public AboutFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        return rootView;
    }
}

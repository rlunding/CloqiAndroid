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
public class HomeFragment extends Fragment {

    //Constants
    public static final String TAG = HomeFragment.class.getSimpleName();

    //Fields

    public HomeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        return rootView;
    }
}

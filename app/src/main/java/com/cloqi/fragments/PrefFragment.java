package com.cloqi.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cloqi.R;

/**
 *
 * Created by Lunding on 18/04/15.
 */
public class PrefFragment extends PreferenceFragment {

    //Constants
    public static final String TAG = PrefFragment.class.getSimpleName();

    //Fields

    public PrefFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}

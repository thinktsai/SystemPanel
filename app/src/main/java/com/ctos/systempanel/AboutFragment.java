package com.ctos.systempanel;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by steve on 2017/6/18.
 */

    public  class AboutFragment extends PreferenceFragment {
        public static final String TAG = "AboutFragment";

        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preference_about);
        }
    }


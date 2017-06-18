package com.ctos.systempanel;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.AttributeSet;

import java.util.TimeZone;

public class AboutPreference extends Preference {
    public AboutPreference(Context context) {
        super(context);
        this.init();
    }


    public AboutPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {
    }
}


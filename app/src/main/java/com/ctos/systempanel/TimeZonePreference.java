package com.ctos.systempanel;

import java.util.Calendar;
import java.util.TimeZone;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.ListPreference;
import android.util.AttributeSet;

public class TimeZonePreference extends ListPreference {
    public TimeZonePreference(Context context) {
        super(context);
        this.init();
    }


    public TimeZonePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {
        this.setEntries(TimeZone.getAvailableIDs());
        this.setEntryValues(this.getEntries());
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    public CharSequence getSummary() {
        return this.getValue();
    }
}
package com.ctos.systempanel;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimePreference extends DialogPreference implements Preference.OnPreferenceChangeListener {
    private TimePicker picker = null;
    public final static long DEFAULT_VALUE = 0;

   public TimePreference(final Context context, final AttributeSet attrs) {
       super(context, attrs);
       this.setOnPreferenceChangeListener(this);
       setPositiveButtonText("Set");
       setNegativeButtonText("Cancel");

   }


    protected void setTime(final long time) {

        persistLong(time);

        notifyDependencyChange(shouldDisableDependents());
        notifyChanged();
    }

    protected void updateSummary(final long time) {
        final DateFormat dateFormat = android.text.format.DateFormat.getTimeFormat(getContext());
        final Date date = new Date(time);
        setSummary(dateFormat.format(date.getTime()));
    }

    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());
        picker.setIs24HourView(android.text.format.DateFormat.is24HourFormat(getContext()));
        return picker;
    }

    @Override
    protected void onBindDialogView(final View v) {
        super.onBindDialogView(v);
        final Calendar c =  Calendar.getInstance();

        picker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
        picker.setCurrentMinute(c.get(Calendar.MINUTE));
    }

    @Override
    protected void onDialogClosed(final boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            final Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MINUTE, picker.getCurrentMinute());
            cal.set(Calendar.HOUR_OF_DAY, picker.getCurrentHour());


            if (!callChangeListener(cal.getTimeInMillis())) {
                return;
            }

            setTime(cal.getTimeInMillis());
        }
    }

    @Override
    protected void onSetInitialValue(final boolean restorePersistedValue, final Object defaultValue) {
        final Calendar c = Calendar.getInstance();
        updateSummary(c.getTimeInMillis());
    }

    @Override
    public boolean onPreferenceChange(final Preference preference, final Object newValue) {
       ((TimePreference) preference).updateSummary((Long)newValue);
        return true;
    }
}
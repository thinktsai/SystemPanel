package com.ctos.systempanel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;

public class DatePreference extends DialogPreference implements Preference.OnPreferenceChangeListener {
    private int lastDate = 0;
    private int lastMonth = 0;
    private int lastYear = 0;
    private String dateval;
    private CharSequence mSummary;
    private DatePicker picker = null;
    public static int getYear(String dateval) {
        String[] pieces = dateval.split("-");
        return (Integer.parseInt(pieces[0]));
    }

    public static int getMonth(String dateval) {
        String[] pieces = dateval.split("-");
        return (Integer.parseInt(pieces[1]));
    }

    public static int getDate(String dateval) {
        String[] pieces = dateval.split("-");
        return (Integer.parseInt(pieces[2]));
    }

    public DatePreference(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);
        this.setOnPreferenceChangeListener(this);

        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
    }

    protected void updateSummary(final long time) {
        final DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getContext());
        final Date date = new Date(time);
        setSummary(dateFormat.format(date.getTime()));
        MainActivity.ctosInfo.setSystemTime(date);
    }



    @Override
    protected View onCreateDialogView() {
        picker = new DatePicker(getContext());
        picker.setCalendarViewShown(false);

        return (picker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        final Calendar cal = Calendar.getInstance();
        picker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }

    protected void setDate(final long time) {
        persistLong(time);

        notifyDependencyChange(shouldDisableDependents());
        notifyChanged();
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            final Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, picker.getYear());
            cal.set(Calendar.MONTH, picker.getMonth());
            cal.set(Calendar.DAY_OF_MONTH, picker.getDayOfMonth());

            if (!callChangeListener(cal.getTimeInMillis())) {
                return;
            }
            setDate(cal.getTimeInMillis());
        }
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        final Calendar cal = Calendar.getInstance();
        updateSummary(cal.getTimeInMillis());
    }


    public boolean onPreferenceChange(final Preference preference, final Object newValue) {
        ((DatePreference) preference).updateSummary((Long)newValue);
        return true;
    }

}
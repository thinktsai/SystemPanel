package com.ctos.systempanel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class ApplicationPreference extends ListPreference {

    public ApplicationPreference(Context context) {
        super(context);
        this.init(context);
    }


    public ApplicationPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    private void init(Context context) {
        List<CharSequence> pkgList = new ArrayList<CharSequence>();
        final PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        int i=0;
        for (ApplicationInfo packageInfo : packages) {
            if (!isSystemPackage(packageInfo))
                pkgList.add(packageInfo.packageName);
            Log.d(TAG, "Installed package :" + packageInfo.packageName);
            Log.d(TAG, "Source dir : " + packageInfo.sourceDir);
            Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
        }

        CharSequence pkgEntries[] = new CharSequence[pkgList.size()];
        pkgList.toArray(pkgEntries);

        this.setEntries(pkgEntries);
        this.setEntryValues(this.getEntries());
    }
    private boolean isSystemPackage(ApplicationInfo pkgInfo) {
        return ((pkgInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
                : false;
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

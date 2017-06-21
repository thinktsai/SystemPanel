package com.ctos.systempanel;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;


public class AboutFragment extends PreferenceFragment {
        public static final String TAG = "AboutFragment";


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference_about);

        findPreference("model_number").setSummary(MainActivity.ctosInfo.getModelNumber());
        findPreference("serial_number").setSummary(MainActivity.ctosInfo.getSerialNumber());
        findPreference("secure_module_version").setSummary(MainActivity.ctosInfo.getSecureModuleVersion());
        findPreference("emv_version").setSummary(MainActivity.ctosInfo.getEMVVersion());
        findPreference("emvcl_version").setSummary(MainActivity.ctosInfo.getEMVCLVersion());
        findPreference("kms_version").setSummary(MainActivity.ctosInfo.getKMSVersion());
        findPreference("uld_key_hash").setSummary(MainActivity.ctosInfo.getULDKeyHash());
        findPreference("bootloader_version").setSummary(MainActivity.ctosInfo.getBootloaderVersion());
        findPreference("little_kernel_version").setSummary(MainActivity.ctosInfo.getLittleKernelVersion());
        findPreference("linux_kernel_version").setSummary(MainActivity.ctosInfo.getLinuxKernelVersion());
        findPreference("android_system_version").setSummary(MainActivity.ctosInfo.getAndroidSystemVersion());

    }
}


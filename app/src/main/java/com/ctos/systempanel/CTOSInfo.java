package com.ctos.systempanel;

import android.app.AlarmManager;
import android.content.Context;

import java.security.MessageDigest;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Steve_Tsai on 2017/6/21.
 */

public class CTOSInfo {
    private int rebootInterval = 20;
    private boolean autoRebootState = true;
    private boolean keySoundState = true;
    private boolean debugModeState = true;
    private boolean passwordState = true;
    private boolean autoTimeState = true;

    private int batteryThreshold = 90;
    private CharSequence ntpServer = "192.168.1.1";
    private CharSequence modelNumber = "AAA";
    private CharSequence serialNumber = "AAA";
    private CharSequence emvVersion = "AAA";
    private CharSequence emvCLVersion = "AAA";
    private CharSequence kmsVersion = "AAA";
    private CharSequence bootloaderVersion = "AAA";
    private CharSequence littleKernelVersion = "AAA";
    private CharSequence linuxKernelVersion = "AAA";
    private CharSequence androidSystemVersion = "AAA";
    private CharSequence secureModuleVersion = "AAA";
    private CharSequence uldKeyHash = "AAA";
    private CharSequence defaultApplication = "AAA";
    private CharSequence passwordHash = "";

    public CTOSInfo() {
        setPassword("00000000");
    }

    public static String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for (byte b : in) {
            builder.append(String.format("%02X", b));
        }
        return builder.toString();
    }

    public int getRebootInterval() {
        return rebootInterval;
    }

    public void setRebootInterval(int rebootTime) {
        rebootInterval = rebootTime;
    }

    public boolean getAutoTimeState() {
        return autoTimeState;
    }

    public void setAutoTimeState(boolean enable) {
        autoTimeState = enable;
    }

    public boolean getAutoRebootState() {
        return autoRebootState;
    }

    public void setAutoRebootState(boolean enable) {
        autoRebootState = enable;
    }

    public boolean getKeySoundState() {
        return keySoundState;
    }

    public void setKeySoundState(boolean enable) {
        autoRebootState = enable;
    }

    public boolean getDebugModeState() {
        return debugModeState;
    }

    public void setDebugModeState(boolean enable) {
        debugModeState = enable;
        if (enable)
            PropertyUtility.set("sys.usb.config", "adb");
        else
            PropertyUtility.set("sys.usb.config", "adb");
    }

    public boolean getPasswordState() {
        return passwordState;
    }

    public void setPasswordState(boolean enable) {
        passwordState = enable;
    }

    public void setPassword(CharSequence password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            md.update(password.toString().getBytes("UTF-8")); // Change this to "UTF-16" if needed
            byte[] digest = md.digest();
            passwordHash = bytesToHex(digest);
        } catch (Exception e) {
        }
    }

    public CharSequence getPasswordHash() {
        return passwordHash;
    }

    public CharSequence getPasswordHash(CharSequence password) {

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            md.update(password.toString().getBytes("UTF-8")); // Change this to "UTF-16" if needed
            byte[] digest = md.digest();
            return bytesToHex(digest);
        } catch (Exception e) {
        }
        return "";
    }

    public int getBatteryThreshold() {
        return batteryThreshold;
    }

    public void setBatteryThreshold(int threshold) {
        batteryThreshold = threshold;
    }

    public CharSequence getNTPServer() {
        return ntpServer;
    }

    public void setNTPServer(CharSequence ntp) {
        ntpServer = ntp;
    }

    public CharSequence getModelNumber() {
        return modelNumber;
    }

    public CharSequence getSerialNumber() {
        return serialNumber;
    }

    public CharSequence getSecureModuleVersion() {
        return secureModuleVersion;
    }

    public CharSequence getEMVVersion() {
        return emvVersion;
    }

    public CharSequence getEMVCLVersion() {
        return emvCLVersion;
    }

    public CharSequence getKMSVersion() {
        return kmsVersion;
    }

    public CharSequence getULDKeyHash() {
        return uldKeyHash;
    }

    public CharSequence getBootloaderVersion() {
        return bootloaderVersion;
    }

    public CharSequence getLittleKernelVersion() {
        return littleKernelVersion;
    }

    public CharSequence getLinuxKernelVersion() {
        return linuxKernelVersion;
    }

    public CharSequence getAndroidSystemVersion() {
        return androidSystemVersion;
    }

    public CharSequence getDefaultApplication() {
        return defaultApplication;
    }

    public void setDefaultApplication(CharSequence application) {
        defaultApplication = application;
    }

    /* month : 0 to 11 */
    private void setSystemTime(int year, int month, int day, int hour, int minute, int second) {
    }

    public void setSystemTime(Context context, Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        setSystemTime(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                cal.get(Calendar.SECOND)
        );
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setTime(date.getTime());

    }
}

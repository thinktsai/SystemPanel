package com.ctos.systempanel;

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

    public CTOSInfo() {

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
    }

    public boolean getPasswordState() {
        return passwordState;
    }

    public void setPasswordState(boolean enable) {
        passwordState = enable;
    }

    public void setPassword(CharSequence password) {
    }

    public CharSequence getPasswordHash() {
        return "";
    }

    public CharSequence getPasswordHash(CharSequence password) {
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

}

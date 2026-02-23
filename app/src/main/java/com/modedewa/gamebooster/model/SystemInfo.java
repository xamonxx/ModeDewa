package com.modedewa.gamebooster.model;

/**
 * SystemInfo — Data class holding device hardware information.
 */
public class SystemInfo {
    public String deviceModel = "";
    public String manufacturer = "";
    public String androidVersion = "";
    public int sdkLevel = 0;
    public String chipset = "";
    public long totalRam = 0;      // in MB
    public long availableRam = 0;  // in MB
    public int batteryPercent = 0;
    public float batteryTemp = 0;  // in Celsius

    public String getRamDisplay() {
        return availableRam + "/" + totalRam + " MB";
    }

    public String getTempDisplay() {
        if (batteryTemp <= 0) return "N/A";
        return String.format("%.1f°C", batteryTemp);
    }

    public int getRamUsagePercent() {
        if (totalRam == 0) return 0;
        return (int) (((totalRam - availableRam) * 100) / totalRam);
    }
}

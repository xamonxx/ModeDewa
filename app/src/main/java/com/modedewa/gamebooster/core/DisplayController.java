package com.modedewa.gamebooster.core;

import com.modedewa.gamebooster.util.ShizukuShell;
import android.util.Log;

/**
 * DisplayController — Manages display settings for gaming.
 * Maps to modeGameON.sh Steps 3, 15.
 */
public class DisplayController {

    private static final String TAG = "DisplayController";

    // ═══════════════════════════════════════
    // ACTIVATE
    // ═══════════════════════════════════════

    public static boolean optimizeForGaming() {
        Log.d(TAG, "Optimizing display for gaming...");
        boolean ok = true;

        // Max brightness
        ok &= ShizukuShell.putSetting("system", "screen_brightness", "200");
        // Disable adaptive brightness (consistent brightness)
        ok &= ShizukuShell.putSetting("system", "screen_brightness_mode", "0");
        // Keep screen on longer (10 minutes)
        ok &= ShizukuShell.putSetting("system", "screen_off_timeout", "600000");
        // Disable auto-rotate
        ok &= ShizukuShell.putSetting("system", "accelerometer_rotation", "0");

        return ok;
    }

    // ═══════════════════════════════════════
    // DEACTIVATE
    // ═══════════════════════════════════════

    public static boolean restoreDefaults() {
        Log.d(TAG, "Restoring display defaults...");
        boolean ok = true;
        ok &= ShizukuShell.putSetting("system", "screen_brightness", "128");
        ok &= ShizukuShell.putSetting("system", "screen_brightness_mode", "1");
        ok &= ShizukuShell.putSetting("system", "screen_off_timeout", "60000");
        ok &= ShizukuShell.putSetting("system", "accelerometer_rotation", "1");
        return ok;
    }
}

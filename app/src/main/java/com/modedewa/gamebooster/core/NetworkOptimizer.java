package com.modedewa.gamebooster.core;

import com.modedewa.gamebooster.util.ShizukuShell;
import android.util.Log;

/**
 * NetworkOptimizer — Optimizes network settings for low-latency gaming.
 * Maps to modeGameON.sh Step 11.
 */
public class NetworkOptimizer {

    private static final String TAG = "NetworkOptimizer";

    // ═══════════════════════════════════════
    // ACTIVATE
    // ═══════════════════════════════════════

    public static boolean optimizeForGaming() {
        Log.d(TAG, "Optimizing network for gaming...");
        boolean ok = true;

        // WiFi never sleep
        ok &= ShizukuShell.putSetting("global", "wifi_sleep_policy", "2");

        // Disable captive portal (faster WiFi connect)
        ok &= ShizukuShell.putSetting("global", "captive_portal_mode", "0");

        // Prefer IPv4 (more stable for gaming)
        ok &= ShizukuShell.putSetting("global", "preferred_network_mode1", "9");

        // Mobile data always active
        ok &= ShizukuShell.putSetting("global", "mobile_data_always_on", "1");

        return ok;
    }

    // ═══════════════════════════════════════
    // DEACTIVATE
    // ═══════════════════════════════════════

    public static boolean restoreDefaults() {
        Log.d(TAG, "Restoring network defaults...");
        boolean ok = true;
        ok &= ShizukuShell.putSetting("global", "wifi_sleep_policy", "2");
        ok &= ShizukuShell.putSetting("global", "captive_portal_mode", "1");
        ok &= ShizukuShell.putSetting("global", "mobile_data_always_on", "1");
        return ok;
    }
}

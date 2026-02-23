package com.modedewa.gamebooster.core;

import com.modedewa.gamebooster.util.ShizukuShell;
import android.util.Log;

/**
 * AudioController — Manages audio settings for gaming.
 * Maps to modeGameON.sh Step 14.
 */
public class AudioController {

    private static final String TAG = "AudioController";

    public static boolean optimizeForGaming() {
        Log.d(TAG, "Optimizing audio for gaming...");
        boolean ok = true;
        ok &= ShizukuShell.putSetting("system", "sound_effects_enabled", "0");
        ok &= ShizukuShell.putSetting("system", "dtmf_tone", "0");
        ok &= ShizukuShell.putSetting("system", "lockscreen_sounds_enabled", "0");
        ok &= ShizukuShell.putSetting("global", "charging_sounds_enabled", "0");
        return ok;
    }

    public static boolean restoreDefaults() {
        Log.d(TAG, "Restoring audio defaults...");
        boolean ok = true;
        ok &= ShizukuShell.putSetting("system", "sound_effects_enabled", "1");
        ok &= ShizukuShell.putSetting("system", "dtmf_tone", "1");
        ok &= ShizukuShell.putSetting("system", "lockscreen_sounds_enabled", "1");
        ok &= ShizukuShell.putSetting("global", "charging_sounds_enabled", "1");
        return ok;
    }
}

package com.modedewa.gamebooster.model;

import android.graphics.drawable.Drawable;

/**
 * GameInfo — Data class holding detected game information.
 */
public class GameInfo {
    public String packageName = "";
    public String displayName = "";
    public Drawable icon = null;
    public boolean isInstalled = false;
    public boolean isSelected = false;

    @Override
    public String toString() {
        return displayName + " (" + packageName + ")";
    }
}

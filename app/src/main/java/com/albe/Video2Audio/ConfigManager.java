package com.albe.Video2Audio;

import android.content.Context;
import android.content.SharedPreferences;

public class ConfigManager {
    private static final String PREFS_NAME = "prefs";

    private final SharedPreferences sharedPreferences;

    public ConfigManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public String loadString(String string) {
        return sharedPreferences.getString(string, null);
    }

    public boolean hasString(String string) {
        return sharedPreferences.contains(string);
    }
}

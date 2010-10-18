package com.openrobot.common;

import android.app.Activity;
import android.content.SharedPreferences;

public class PreferenceHelper {

	// ************************************************************************
    // PREFERENCES UTILITY FUNCTIONS
    // ************************************************************************
    
    public static String getPreferenceStringForKey(Activity activity, String key, String defVal) {
    	SharedPreferences settings = activity.getPreferences(Activity.MODE_PRIVATE);
    	return settings.getString(key, defVal);
    }
    
    public static void setPreferenceStringForKey(Activity activity, String key, String string) {
    	SharedPreferences settings = activity.getPreferences(Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, string);
        editor.commit();
    }
    
    public static int getPreferenceIntForKey(Activity activity, String key, int defVal) {
    	SharedPreferences settings = activity.getPreferences(Activity.MODE_PRIVATE);
    	return settings.getInt(key, defVal);
    }
    
   	public static void setPreferenceIntForKey(Activity activity, String key, int theInt) {
    	SharedPreferences settings = activity.getPreferences(Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, theInt);
        editor.commit();
    }
    
}

package com.example.arpaul.rideit.Common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ARPaul on 19-03-2016.
 */
public class AppPreference {

    private SharedPreferences preferences;
    private SharedPreferences.Editor edit;

    public static final String IS_STARTED						=	"IS_STARTED";
    public static final String LAST_PHOTO 						=	"LAST_PHOTO";

    public AppPreference(Context context)
    {
        preferences		=	PreferenceManager.getDefaultSharedPreferences(context);
        edit			=	preferences.edit();
    }

    public void saveStringInPreference(String strKey,String strValue)
    {
        edit.putString(strKey, strValue);
    }

    public void removeFromPreference(String strKey)
    {
        edit.remove(strKey);
    }

    public void commitPreference()
    {
        edit.commit();
    }

    public String getStringFromPreference(String strKey,String defaultValue )
    {
        return preferences.getString(strKey, defaultValue);
    }
}

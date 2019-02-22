package com.electrom.vahanwireprovider.utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

/**
 * Created by admin on 7/20/2017.
 */

public class SessionManager {

    public static final String TAG = SessionManager.class.getSimpleName();


    public static final String PROVIDER_MOBILE = "provider_mobile";
    public static final String DEVICE_ID = "device_id";
    public static final String NOTIFICATION_TOKEN = "notification_token";
    public static final String PROVIDER_PIN = "provider_pin";
    public static final String REGISTER_NAME = "register_name";
    public static final String EMAIL = "email";
    public static final String ADDRESS = "address";
    public static final String CONTACT_PERSON = "contact+person";
    public static final String LANDLINE = "landline";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String PROVIDER_IMAGE = "profile_pic";
    private static String SHARED_PREFERENCE_PROVIDER = "shared_provider_electrom";
    ///public static final String COUNT_COMPARE = "count_compare";
    private static SharedPreferences sharedPref;
    private static Context context;
    private static SessionManager sharedPrefClass;


    public SessionManager(Context context) {
        sharedPref = context.getSharedPreferences(SHARED_PREFERENCE_PROVIDER, Activity.MODE_PRIVATE);
    }

    public static SessionManager getInstance(Context context) {
        sharedPref = context.getSharedPreferences(SHARED_PREFERENCE_PROVIDER, Activity.MODE_PRIVATE);
        SessionManager.context = context;
        if (sharedPrefClass == null) {
            sharedPrefClass = new SessionManager(context);
            return sharedPrefClass;
        } else {
            return sharedPrefClass;
        }
    }

    public void setString(String key, String value) {
        Log.e(TAG, " ==> " + key + " / " + value);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key) {
        Log.e(TAG, " ==> " + key);
        sharedPref = SessionManager.context.getSharedPreferences(SHARED_PREFERENCE_PROVIDER, Activity.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }

    public void setInt(String key, int value) {
        Log.e(TAG, " ==> " + key + " / " + value);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public int getInt(String key) {
        Log.e(TAG, " ==> " + key);
        sharedPref = SessionManager.context.getSharedPreferences(SHARED_PREFERENCE_PROVIDER, Activity.MODE_PRIVATE);
        return sharedPref.getInt(key, 0);
    }


    public void setBoolean(String key, boolean value) {
        Log.e(TAG, " ==> " + key + " / " + value);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key) {
        Log.e(TAG, " ==> " + key);
        sharedPref = SessionManager.context.getSharedPreferences(SHARED_PREFERENCE_PROVIDER, Activity.MODE_PRIVATE);
        return sharedPref.getBoolean(key, false);
    }

    public void setObject(String key, Object obj){
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(obj);
        editor.putString(key, json);
        editor.apply();
    }

    public Object getObject(String key, Activity activity)
    {
        Gson gson = new Gson();
         sharedPref = SessionManager.context.getSharedPreferences(SHARED_PREFERENCE_PROVIDER, Activity.MODE_PRIVATE);
         sharedPref.getString(key, null);
        String json = sharedPref.getString(key, null);
        return  gson.fromJson(json, Activity.class);
    }

}



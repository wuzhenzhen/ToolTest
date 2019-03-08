package com.kgd.tools.library;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by wzz on 2017/07/06.
 * wuzhenzhen@tiamaes.com
 */

public class SPUtil {
    private static SharedPreferences sp = null;
    public static SPUtil bsp = null;
    public static String SP_NAME = "sp_name";
    public static SPUtil  getInstance(Context context){
        if(bsp == null || sp == null) {
            bsp = new SPUtil();
            sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        }
        return bsp;
    }

    public boolean contains(String key){
        return sp.contains(key);
    }

    public String getData(String key) {
        return sp.getString(key, null);
    }

    public String getData(String key, String defaultStr) {
        return sp.getString(key, defaultStr);
    }

    public int getData(String key, int defaultInt){
        return sp.getInt(key, defaultInt);
    }

    public boolean getData(String key, boolean defaultBool){
        return sp.getBoolean(key, defaultBool);
    }

    public void putData(String key, String str) {
        SharedPreferences.Editor edit = sp.edit();
        edit.remove(key);
        edit.putString(key, str);
        edit.commit();
    }

    public void putData(String key, boolean value){
        SharedPreferences.Editor edit = sp.edit();
        edit.remove(key);
        edit.putBoolean(key,value);
        edit.commit();
    }

    public void putData(String key, int value) {
        SharedPreferences.Editor edit = sp.edit();
        edit.remove(key);
        edit.putInt(key,value);
        edit.commit();
    }

    public static void removeData(String key) {
        SharedPreferences.Editor edit = sp.edit();
        edit.remove(key);
        edit.commit();
    }

    public static void clearData() {
        SharedPreferences.Editor edit = sp.edit();
        edit.clear();
        edit.commit();
    }

    public boolean getDataBool(String key, boolean b) {
        return sp.getBoolean(key, b);
    }

    public void putDataBool(String key, boolean b) {
        SharedPreferences.Editor edit = sp.edit();
        edit.remove(key);
        edit.putBoolean(key, b);
        edit.commit();
    }

    public Set<String> getStringSet(String key, Set<String> sets){
        return sp.getStringSet(key,sets);
    }

    public void putStringSet(String key, Set<String> sets){
        SharedPreferences.Editor edit = sp.edit();
        edit.remove(key);
        edit.putStringSet(key, sets);
        edit.commit();
    }
}

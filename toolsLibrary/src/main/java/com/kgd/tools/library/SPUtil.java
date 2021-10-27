package com.kgd.tools.library;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.Set;

/**
 * Created by wzz on 2017/07/06.
 * wzz
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

    public void putData(String key, String str) {
        SharedPreferences.Editor edit = sp.edit();
        edit.remove(key);
        edit.putString(key, str);
        edit.commit();
    }

    /**
     *
     * @param key
     * @param value
     * @param format  0~3
     */
    public void putData(String key, String value, int format){
        boolean isValid = false;
        switch (format){
            case 0:     //int 型数据
                isValid = AppUtils.isValidInt(value);
                break;
            case 1:     //boolean 型数据
                isValid = AppUtils.isValidBoolean(value);
                break;
            case 2:     //ip:port 数据
                isValid = AppUtils.isValidIpPort(value);
                break;
            case 3:     //时间段  "14:27~15:27"
                isValid = AppUtils.isValidTimes(value);
                break;
        }
        if(isValid){
            SharedPreferences.Editor edit = sp.edit();
            edit.remove(key);
            edit.putString(key, value);
            edit.commit();
        }else{
//            ZLog.eee("--putData--"+key+"--"+value+"--"+format);
        }
    }

    /**
     *
     * @param key
     * @param value
     * @param format    0~3
     * @param isAllowEmpty  是否允许 null ， 如果为空则保存 空字符串
     */
    public void putData(String key, String value, int format, boolean isAllowEmpty){
        boolean isValid = false;
        switch (format){
            case 0:     //int 型数据
                isValid = AppUtils.isValidInt(value);
                break;
            case 1:     //boolean 型数据
                isValid = AppUtils.isValidBoolean(value);
                break;
            case 2:     //ip:port 数据
                isValid = AppUtils.isValidIpPort(value);
                break;
            case 3:     //时间段  "14:27~15:27"
                isValid = AppUtils.isValidTimes(value);
                break;
        }
        if(isValid){
            SharedPreferences.Editor edit = sp.edit();
            edit.remove(key);
            edit.putString(key, value);
            edit.commit();
        }else{
            if(TextUtils.isEmpty(value) && isAllowEmpty){
                SharedPreferences.Editor edit = sp.edit();
                edit.remove(key);
                edit.putString(key, "");
                edit.commit();
            }
//            ZLog.eee("--putData--"+key+"--"+value+"--"+format+"--"+isAllowEmpty);
        }
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

    public int getData(String key, int defaultInt){
        return sp.getInt(key, defaultInt);
    }

    public boolean getData(String key, boolean defaultBool){
        return sp.getBoolean(key, defaultBool);
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

    public void remove(String key){
        SharedPreferences.Editor edit = sp.edit();
        edit.remove(key);
        edit.commit();
    }

    //自定 SP 文件路径
    public static void putCustome(Context context, String spName, String key, String value){
        SharedPreferences sp = context.getSharedPreferences(spName, Activity.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.remove(key);
        edit.putString(key, value);
        edit.commit();
    }
    //自定 SP 文件路径
    public static String getCustome(Context context, String spName, String key){
        SharedPreferences sp = context.getSharedPreferences(spName, Activity.MODE_PRIVATE);
        return sp.getString(key, "");
    }
}

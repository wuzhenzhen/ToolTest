package com.kgd.tools.library;

import android.app.AlarmManager;
import android.content.Context;
import android.provider.Settings;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by wzz on 2018/06/26.
 * wuzhenzhen@tiamaes.com
 */
public class SystemUtils {

    private SystemUtils(){
        /**cannot be instantiated **/
        throw new UnsupportedOperationException("cannot be instantiated");
    }


    //---------------------------------ʱ���������-------------------------------
    /**
     *  �ж�ϵͳ��ʱ���Ƿ����Զ���ȡ��
     * @param context
     * @return
     */
    public static boolean isTimeZoneAuto(Context context){
        try {
            return  android.provider.Settings.Global.getInt(context.getContentResolver(),
                    android.provider.Settings.Global.AUTO_TIME_ZONE) > 0;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     *  ����ϵͳʱ���Ƿ��Զ�ͬ��
     * @param context
     * @param checked  0 ȡ���Զ�ͬ���� 1�Զ�ͬ��
     */
    public static void setAutoTimeZone(Context context, int checked){
        android.provider.Settings.Global.putInt(context.getContentResolver(),
                android.provider.Settings.Global.AUTO_TIME_ZONE, checked);
    }

    /**
     *  �ж�ϵͳ��ʱ���Ƿ��Զ���ȡ��
     * @param context
     * @return
     */
    public static boolean isDateTimeAuto(Context context){
        try {
            return android.provider.Settings.Global.getInt(context.getContentResolver(),
                    android.provider.Settings.Global.AUTO_TIME) > 0;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     *  ����ϵͳ��ʱ���Ƿ���Ҫ�Զ���ȡ
     * @param context
     * @param checked  0 ȡ���Զ�ͬ���� 1�Զ�ͬ��
     */
    public static void setAutoDateTime(Context context, int checked){
        android.provider.Settings.Global.putInt(context.getContentResolver(),
                android.provider.Settings.Global.AUTO_TIME, checked);
    }

    /**
     * ����ϵͳ����
     * @param context
     * @param cl
     */
    public static void setSysDate(Context context, Calendar cl){
        setSysDate(context,
                cl.get(Calendar.YEAR),
                cl.get(Calendar.MONTH),
                cl.get(Calendar.DAY_OF_MONTH),
                cl.get(Calendar.HOUR_OF_DAY),
                cl.get(Calendar.MINUTE),
                cl.get(Calendar.SECOND));
    }

    /**
     * ����ϵͳ����
     * @param context
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @param second
     */
    public static void setSysDate(Context context, int year, int month, int day, int hour, int minute, int second){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);

        long when = c.getTimeInMillis();

        if(when / 1000 < Integer.MAX_VALUE){
            ((AlarmManager)context.getSystemService(Context.ALARM_SERVICE)).setTime(when);
        }
    }

    /**
     * ����ϵͳʱ��
     * @param timeZone
     *   �磺 setTimeZone("GMT+8:00");
     */
    public static void setTimeZone(String timeZone){
        final Calendar now = Calendar.getInstance();
        TimeZone tz = TimeZone.getTimeZone(timeZone);
        now.setTimeZone(tz);
    }

    /**
     * ��ȡϵͳ��ǰ��ʱ��
     * @return
     */
    public static String getDefaultTimeZone(){
//        Logcat.iii("--getDefaultTimeZone--"+ TimeZone.getDefault());
//        Logcat.iii("--getDefaultTimeZone--"+ TimeZone.getDefault().getID());
        return TimeZone.getDefault().getDisplayName();
    }

    /**
     *  ����24Сʱʱ����
     * @param context
     */
    public static void set24H(Context context){
        android.provider.Settings.System.putString(context.getContentResolver(),
                android.provider.Settings.System.TIME_12_24, "24");
    }

    /**
     * ����12Сʱʱ����
     * @param context
     */
    public static void set12H(Context context){
        android.provider.Settings.System.putString(context.getContentResolver(),
                android.provider.Settings.System.TIME_12_24, "12");
    }

    //---------------------------------ʱ���������------END-------------------------


}

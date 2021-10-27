package com.kgd.tools.library;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;



/**
 * Created by wzz on 2016/08/17.
 * wzz
 *
 * ��App/apk ��صĸ�����
 *
 * getAppName  ��ȡӦ�ó�������
 * getVersionName ��ǰӦ�õİ汾����
 * getVersionCode ��ǰӦ�õİ汾��
 * isInstallSoftware ��� packageName �Ƿ��Ѿ���װ
 * installSlient
 * unInstallSlient
 * installApk   ��װ Apk
 * uninstallApk ж�� Apk
 * isServiceRunning  Service�Ƿ���������
 * stopRunningService ֹͣservice
 * getCPUNumCores  �鿴CPU������
 * importDatabase  �������ݿ�
 * setImmersiveMode ����ģʽ
 * getStatusBarHeight ״̬���߶�
 * closeSoftInput ���������
 * getEth0Mac   ��ȡ eth0  mac��ַ
 * getWifiMac   ��ȡ wlan  mac��ַ
 * <p>
 * getIMSI(Context context) �ֻ�����IMSI    �磺IMSI=460031304158115
 * getIMEI(Context context) ��ȡ�ֻ���IMEI    �磺IMEI=866946025546414
 * isOutOfDate(String operaTime)    �����Ƚ���Ӫʱ�䣬�жϵ�ǰʱ���Ƿ񳬳� operatiome(06:00-21:00)��Χ
 * readPictureDegree(String path)   ��ȡͼƬ���ԣ���ת�ĽǶ�
 * rotateBitmap(Bitmap bitmap, int degrees)  ��תͼƬ
 * boolean hasChinese(String str)   �ж��ַ����Ǻ������ģ����Ƿ���false
 */
public class AppUtils
{

    private AppUtils(){
        /**cannot be instantiated **/
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * ��ȡӦ�ó�������
     */
    public static String getAppName(Context context){
        try
        {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * [��ȡӦ�ó���汾������Ϣ]
     *
     * @param context
     * @return ��ǰӦ�õİ汾����
     */
    public static String getVersionName(Context context){
        try
        {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;

        } catch (NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * [��ȡ��ǰӦ�õİ汾��]
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        try
        {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionCode;

        } catch (NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * ��� packageName �Ƿ��Ѿ���װ
     *
     * @param context
     * @param packageName
     * @return
     */
    public static Boolean isInstallSoftware(Context context, String packageName) {
        if (packageName == null || "".equals(packageName)) {
            return false;
        }
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo pInfo = packageManager.getPackageInfo(packageName,
                    PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
            if (pInfo != null) {
                return true;
            }
        } catch (NameNotFoundException e) {

        }
        return false;
    }


    /**
     * install slient��<br>
     * ������½����߳������в�ͨ��handler���Ͱ�װ��������̣߳�����װʱ��ϳ��ᵼ��ANR��<br>
     * ��Ĭ��װ������<br>
     * 1����Ҫ�ѵ�ǰ������Ҳ��installSlient���������ڵ�apk����ϵͳӦ�á�����ϵͳroot������ļ��ŵ�/system/appĿ¼�¡�/
     * system/appĿ¼�µ�apk����ϵͳӦ�á�<br>
     * 2����Ҫ��AndroidManifest.xml�ļ���ע�� android.permission.INSTALL_PACKAGES Ȩ�ޡ�
     *
     * @param filePath Ҫ��װ��apk��·����
     * @return 0 means normal, 1 means file not exist, 2 means other exception
     * error
     */
    public static int installSlient(String filePath) {
        File file = new File(filePath);
        if (filePath == null || filePath.length() == 0
                || (file = new File(filePath)) == null || file.length() <= 0
                || !file.exists() || !file.isFile()) {

            return 1;
        }

        ShellUtils.CommandResult result = ShellUtils.execCommand("pm install -r " + filePath, true);
//        ZLog.iii("--installSlient--" + filePath + "--result=" + result.toString());
        return result.result;
    }

    /**
     *  ǿ�� �׼���װ
     *   pm install -r -d
     * @param filePath
     * @return
     */
    public static int forceInstallSlient(String filePath){
        File file = new File(filePath);
        if (file == null || !file.exists()) {
            return 1;
        }
        try {
            ShellUtils.CommandResult result = null;
            if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower
                result = ShellUtils.execCommand("pm install -r -d " + filePath, false);
            } else if (Build.VERSION.SDK_INT >= 19) {  //Android 4.4 ֮��
                result = ShellUtils.execCommand("pm install -r -d " + filePath, true);
            }
//            ZLog.iii("--forceInstallSlient--" + filePath + "--result=" + result.successMsg + "--" + result.errorMsg);
            return result.result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 2;
    }

    /**
     * uninstall slient��<br>
     * ������½����߳������в�ͨ��handler���Ͱ�װ��������̣߳�����װʱ��ϳ��ᵼ��ANR��<br>
     * ��Ĭж��������<br>
     * 1����ҪϵͳӦ�á�����ϵͳroot������ļ��ŵ�/system/appĿ¼�¡�/
     * system/appĿ¼�µ�apk����ϵͳӦ�á�<br>
     * 2����Ҫ��AndroidManifest.xml�ļ���ע�� android.permission.INSTALL_PACKAGES Ȩ�ޡ�
     *
     * @param packageName ж�صİ���
     * @return 0 means normal, 1 means file not exist, 2 means other exceptionerror
     */
    public static int unInstallSlient(String packageName) {
        ShellUtils.CommandResult result = ShellUtils.execCommand("pm uninstall " + packageName, true);
//        ZLog.iii("--unInstallSlient--" + packageName + "--result=" + result.successMsg + "--" + result.errorMsg);
        return result.result;
    }

    /**
     * ��������װapk.
     *
     * @param context the context
     * @param file apk�ļ�·��
     */
    public static void installApk(Context context, File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * ������ж�س���.
     *
     * @param context the context
     * @param packageName ����
     */
    public static void uninstallApk(Context context,String packageName) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        Uri packageURI = Uri.parse("package:" + packageName);
        intent.setData(packageURI);
        context.startActivity(intent);
    }

    /**
     *  ���apk�ļ�����װ
     *  1. ����ļ�������
     *  2. �������Ƿ�һ��
     *  3. ���汾���Ǳ��������еĴ�
     * @param apkPath   �ļ�·��
     * @param context
     * @param isForce   �Ƿ�ǿ�ư�װ ��ʹ��ǰ�ļ��汾��С�� �������еİ汾��
     */
    public static void checkAndInstallApk(String apkPath, Context context, boolean isForce){
        if(TextUtils.isEmpty(apkPath) || context == null){
//            ZLog.eee("----checkAndInstallApk----apkPath-or-context-is-null="+apkPath);
            return;
        }
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES);
        if(packageInfo == null){
//            ZLog.eee("----checkAndInstallApk--packageInfo is null--"+apkPath);
            File apkFile = new File(apkPath);
            if(apkFile.exists()&&apkFile.isFile()){
//                ZLog.eee("--delete--Invalid--version--"+AppUtils.getVersionCode(context));
                apkFile.delete();
            }
            return;
        }
        int nVersionCode = packageInfo.versionCode;
        String nPackageName = packageInfo.packageName;

        if(context.getPackageName().equals(nPackageName)){
            if(AppUtils.getVersionCode(context) < nVersionCode){
//                ZLog.iii("----checkAndInstallApk--1-"+apkPath);
                AppUtils.installSlient(apkPath);
            }else if (isForce){
                AppUtils.forceInstallSlient(apkPath);
//                ZLog.eee("--checkAndInstallApk--2-"+apkPath);
            }
        }else{
//            ZLog.eee("--checkAndInstallApk packageName error="+nPackageName);
        }
    }

    /**
     * Service�Ƿ���������
     *
     * @param ctx
     * @param className �жϵķ������� "com.xxx.xx..XXXService"
     * @return true������    false��������
     */
    public static boolean isServiceRunning(Context ctx, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> servicesList = activityManager.getRunningServices(Integer.MAX_VALUE);
        Iterator<ActivityManager.RunningServiceInfo> l = servicesList.iterator();
        while (l.hasNext()) {
            ActivityManager.RunningServiceInfo si = (ActivityManager.RunningServiceInfo) l.next();
            if (className.equals(si.service.getClassName())) {
                isRunning = true;
            }
        }
        return isRunning;
    }

    /**
     * ֹͣ����.
     *
     * @param ctx the ctx
     * @param className the class name
     * @return true, if successful
     */
    public static boolean stopRunningService(Context ctx, String className) {
        Intent intent_service = null;
        boolean ret = false;
        try {
            intent_service = new Intent(ctx, Class.forName(className));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (intent_service != null) {
            ret = ctx.stopService(intent_service);
        }
        return ret;
    }


    /**
     * ��ȡCPU������
     * Gets the number of cores available in this device, across all processors.
     * Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"
     *
     * @return The number of cores, or 1 if failed to get result
     */
    public static int getCPUNumCores() {
        try {
            //Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            //Filter to only list the devices we care about
            File[] files = dir.listFiles(new FileFilter() {

                @Override
                public boolean accept(File pathname) {
                    //Check if filename is "cpu", followed by a single digit number
                    if (Pattern.matches("cpu[0-9]", pathname.getName())) {
                        return true;
                    }
                    return false;
                }
            });
            //Return the number of cores (virtual CPU devices)
            return files.length;
        } catch (Exception e) {
            //Default to return 1 core
            return 1;
        }
    }

    /**
     * ״̬���߶�
     *
     * @param activity
     * @return
     */
    private static int getStatusBarHeight(Activity activity) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            return activity.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            Log.d("--getStatusBarHeight--", "get status bar height fail");
            e1.printStackTrace();
            return 75;
        }
    }

    /**
     * �رռ����¼�.
     *
     * @param context
     */
    public static void closeSoftInput(Context context) {
//        InputMethodManager inputMethodManager = (InputMethodManager)context
//                .getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (inputMethodManager != null && ((Activity)context).getCurrentFocus() != null) {
//            inputMethodManager.hideSoftInputFromWindow(((Activity)context).getCurrentFocus()
//                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//        }

        /**���������**/
        View view = ((Activity) context).getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    /**
     * install slient��<br>
     * ������½����߳������в�ͨ��handler���Ͱ�װ��������̣߳�����װʱ��ϳ��ᵼ��ANR��<br>
     * ��Ĭ��װ������<br>
     * 1����Ҫ�ѵ�ǰ������Ҳ��installSlient���������ڵ�apk����ϵͳӦ�á�����ϵͳroot������ļ��ŵ�/system/appĿ¼�¡�/
     * system/appĿ¼�µ�apk����ϵͳӦ�á�<br>
     * 2����Ҫ��AndroidManifest.xml�ļ���ע�� android.permission.INSTALL_PACKAGES Ȩ�ޡ�
     *
     * @param filePath Ҫ��װ��apk��·����
     * @return 0 means normal, 1 means file not exist, 2 means other exception
     * error
     */
    public static int installSlient2(String filePath) {
        File file = new File(filePath);
        if (filePath == null || filePath.length() == 0
                || (file = new File(filePath)) == null || file.length() <= 0
                || !file.exists() || !file.isFile()) {

            return 1;
        }

        String[] args = {"pm", "install", "-r", filePath};
        ProcessBuilder processBuilder = new ProcessBuilder(args);

        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder errorMsg = new StringBuilder();
        int result = -1;
        try {
            process = processBuilder.start();
            successResult = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(
                    process.getErrorStream()));
            String s;

            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
            }

            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = 2;
        } catch (Exception e) {
            e.printStackTrace();
            result = 2;
        } finally {
            try {
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }

        if (successMsg.toString().contains("Success")
                || successMsg.toString().contains("success")) {
            result = 0;
        } else {
            result = 2;
        }
//        installSlient: successMsg:, ErrorMsg:	pkg: /storage/usbhost1/bikes/bikes-0320.apkError: java.lang.SecurityException: Neither user 10051 nor current process has android.permission.INSTALL_PACKAGES.

        Log.e("installSlient", "successMsg:" + successMsg + ", ErrorMsg:"
                + errorMsg);
        return result;
    }

    /**
     * �ֻ�����IMSI    �磺IMSI=460031304158115
     *
     * @param context
     * @return
     */
    public static String getIMSI(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getSubscriberId();
    }

    /**
     * ��ȡ�ֻ���IMEI    �磺IMEI=866946025546414
     *
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getDeviceId();
    }

    /**
     * ��⵱�����磨WLAN��3G/2G��״̬
     *
     * @param context Context
     * @return true ��ʾ�������
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // ��ǰ���������ӵ�
                if (info.getState() == NetworkInfo.State.CONNECTED &&
                        info.isAvailable()) {
                    if (ping()) {
                        return true;    // ��ǰ�����ӵ��������
                    }else{
                        return false;
                    }
                }
            }
        }
        return false;
    }

    /**
     * �ж��Ƿ����������ӣ���ͨ���������ж������������Ƿ����ӣ����������Ͼ�������
     *
     * @return ��������߳���ִ�У����������anr
     */
    public static final boolean ping() {
        String result = null;
        try {
            String ip = "www.baidu.com";// ping �ĵ�ַ�����Ի����κ�һ�ֿɿ�������
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping��ַ3��
            // ��ȡping�����ݣ����Բ���
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer stringBuffer = new StringBuffer();
            String content = "";
            while ((content = in.readLine()) != null) {
                stringBuffer.append(content);
            }
            Thread.sleep(200);
            // ping��״̬
            int status = p.waitFor();
//            Logcat.iii("---ping---result="+status+" content : " + stringBuffer.toString());
            if (status == 0) {
                result = "success";
                return true;
            } else {
                result = "failed";
            }
        } catch (IOException e) {
            result = "IOException";
        } catch (InterruptedException e) {
            result = "InterruptedException";
        } finally {
//            Logcat.iii("----result---", "result = " + result);
        }
//        Logcat.iii("---ping---result false");
        return false;
    }
    
    /**
     * �����̫����Mac��ַ
     *
     * @return
     */
    public static String getEth0Mac() {
        String macSerial = "";
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/eth0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// ȥ�ո�
                    break;
                }
            }
        } catch (IOException ex) {
            // ����Ĭ��ֵ
            ex.printStackTrace();
        }
        macSerial = macSerial.replace(":", "");
        return macSerial;
    }

    /**
     * ���wifi mac��ַ
     *
     * @return
     */
    public static String getWifiMac() {
        String macSerial = null;
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// ȥ�ո�
                    break;
                }
            }
        } catch (IOException ex) {
            // ����Ĭ��ֵ
            ex.printStackTrace();
        }
        return macSerial;
    }

    /**
     * �����ֻ��ƶ����ݿ���  Androd 4.2 �²����
     *
     * @param pContext
     * @param pBoolean ture �����ƶ����ݣ�false �ر��ƶ�����
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void setMobileData(Context pContext, boolean pBoolean) {
        try {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            Class ownerClass = mConnectivityManager.getClass();
            Class[] argsClass = new Class[1];
            argsClass[0] = boolean.class;
            Method method = ownerClass.getMethod("setMobileDataEnabled", argsClass);
            method.invoke(mConnectivityManager, pBoolean);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * �����ֻ��ƶ����ݵ�״̬
     *
     * @param pContext
     * @param arg      Ĭ����null
     * @return true ���� false δ����
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public boolean getMobileDataState(Context pContext, Object[] arg) {
        try {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            Class ownerClass = mConnectivityManager.getClass();
            Class[] argsClass = null;
            if (arg != null) {
                argsClass = new Class[1];
                argsClass[0] = arg.getClass();
            }
            Method method = ownerClass.getMethod("getMobileDataEnabled", argsClass);
            Boolean isOpen = (Boolean) method.invoke(mConnectivityManager, arg);
            return isOpen;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * ����һ��APN�����
     * APN��umim2m.njm2mapn����umim2m.gzm2mapn
     * MCC��460
     * MNC��06
     *
     * @param context
     * @return
     */
    public static int addAPN(Context context, String apnName, String apn) {

        final Uri APN_URI = Uri.parse("content://telephony/carriers");
        int id = -1;

        String NUMERIC = getSIMInfo(context);

        if (NUMERIC == null) {
            return -1;
        }

        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();

        values.put("name", apnName); // apn��������
        values.put("apn", apn); // apn����
        values.put("type", "default,supl");
        values.put("numeric", NUMERIC);
        values.put("mcc", NUMERIC.substring(0, 3)); // 460/06
        values.put("mnc", NUMERIC.substring(3, NUMERIC.length()));
        values.put("proxy", "");
        values.put("port", "");
        values.put("mmsproxy", "");
        values.put("mmsport", "");
        values.put("user", "");
        values.put("server", "");
        values.put("password", "");
        values.put("mmsc", "");
        Cursor c = null;
        Uri newRow = resolver.insert(APN_URI, values);
        if (newRow != null) {
            c = resolver.query(newRow, null, null, null, null);
            int idIndex = c.getColumnIndex("_id");
            c.moveToFirst();
            id = c.getShort(idIndex);
        }
        if (c != null)
            c.close();
        Log.e("", "add new APN id=" + id + "; APN--values=" + values.toString());
        return id;
    }

    /**
     * ���г����е�APN��Ĭ�ϵ��кܶ�APN,ʹ��ʱ��ø���apn���й��ˣ�
     *
     * @param context
     * @return
     */
    public Map<String, String> checkAPN(Context context) {
        final Uri APN_URI = Uri.parse("content://telephony/carriers");
        Map<String, String> map = new HashMap<String, String>();
        Cursor cr = context.getContentResolver().query(APN_URI, null, null,
                null, null);
        int i = 0;
        while (cr != null && cr.moveToNext()) {
            int id = cr.getInt(cr.getColumnIndex("_id"));
            map.put("id" + i, String.valueOf(id));
            String apn = cr.getString(cr.getColumnIndex("apn"));
            map.put("apn" + i, apn);

            String mcc = cr.getString(cr.getColumnIndex("mcc"));
            String mnc = cr.getString(cr.getColumnIndex("mnc"));

            Log.e("APN", "--APN--id= " + id + "; apn=" + apn + "; mcc=" + mcc
                    + "; mnc=" + mnc);
//			if (apn.equals("umim2m.gzm2mapn")) {
//				SetAPN(context, id);
//			}
        }
        return map;
    }

    public static String getSIMInfo(Context context) {
        TelephonyManager iPhoneManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return iPhoneManager.getSimOperator();
    }

    /**
     * ����Ĭ�ϵĽ����
     *
     * @param context
     * @param id
     */
    public static void SetAPN(Context context, int id) {
        final Uri CURRENT_APN_URI = Uri
                .parse("content://telephony/carriers/preferapn");
        Log.e("APN", "--SetApn--" + id);
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put("apn_id", id);
        resolver.update(CURRENT_APN_URI, values, null, null);
        // resolver.delete(url, where, selectionArgs)
    }

    /**
     * ����Ƿ����SD��
     *
     * @return
     */
    public static boolean ExistSDCard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * ת��dipΪpx����֤�ߴ��С���䡣 <br>
     * Resources�����getDimension()������ʵ��dip2px()�����ǵȼ۵ģ� �����԰�һ��dipת��Ϊpx��<br>
     * ���磬<br>
     * float dimen =
     * getResources().getDimension(R.dimen.activity_horizontal_margin);<br>
     * System.out.println("getDimension()" + dimen);<br>
     * <br>
     * float f = dip2px(this, 16.0f);<br>
     * System.out.println("f" + f);<br>
     *
     * @param context
     * @param dip
     * @return
     */
    public static float dip2px(Context context, float dip) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }

    /**
     * ת��pxΪdip����֤�ߴ��С���䡣
     *
     * @param context
     * @param px
     * @return
     */
    public static float px2dip(Context context, float px) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (px / scale + 0.5f * (px >= 0 ? 1 : -1));
    }

    /**
     * ��pxֵת��Ϊspֵ����֤���ִ�С����
     *
     * @param pxValue ��DisplayMetrics��������scaledDensity��
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * ��spֵת��Ϊpxֵ����֤���ִ�С����
     *
     * @param spValue ��DisplayMetrics��������scaledDensity��
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * ��ȡ��Ļ�ߵķ���������ֵΪpx ��
     *
     * @param context
     */
    public static int getScreenHeight(Context context) {
        // ��һ��
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        // ��Ļ�����أ��磺480px��
        int width = display.getWidth();
        // ��Ļ�ߣ����أ��磺800px��
        int height = display.getHeight();
        Log.d("width", String.valueOf(width));
        Log.d("height", String.valueOf(height));

        // �ڶ���
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);

        int height2 = dm.heightPixels;

        return height2;
    }

    /**
     * ��ȡ��Ļ��ķ�����
     *
     * @param context
     */
    public static int getScreenWidth(Context context) {
        // ��һ��
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        // ��Ļ�����أ��磺480px��
        int width = display.getWidth();
        // ��Ļ�ߣ����أ��磺800px��
        int height = display.getHeight();
        Log.d("width", String.valueOf(width));
        Log.d("height", String.valueOf(height));

        // �ڶ���
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        int width2 = dm.widthPixels;

        return width2;
    }

    /**
     * ���ַ����������ʺ����г�����<br>
     * �����https://zhidao.baidu.com/question/2266663031632027308.html <br>
     * <br>
     *
     * @param view
     * @return ��ȡ�ؼ��� ��λ px ��
     */
    public static int getWidth(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return (view.getMeasuredWidth());
    }

    /**
     * ���ַ����������ʺ����г�����<br>
     * �����https://zhidao.baidu.com/question/2266663031632027308.html <br>
     * <br>
     *
     * @param view
     * @return ��ȡ�ؼ��� ��λ px ��
     */
    public static int getHeight(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return (view.getMeasuredHeight());
    }

    /**
     * ���ַ��������У����ǲ�����ʹ�á�<br>
     * ��Ϊ����ص���������úܶ��,���һ���TextView��ʱ����Ȼ�����,���Բ�����ʹ�á�<br>
     * �� http://sunjilife.blog.51cto.com/3430901/1159896 <br>
     * ��ȡ�ؼ��� ��λ px ��
     *
     * @param view
     * @return
     */
    @Deprecated
    public static int getWidthGood(final View view) {
        // final int[] height = new int[1];
        final int[] width = new int[1];
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                // height[0] = view.getMeasuredHeight();
                width[0] = view.getMeasuredWidth();

                return true;
            }
        });
        return (width[0]);
    }

    /**
     * ���ַ��������У����ǲ�����ʹ�á�<br>
     * ��Ϊ����ص���������úܶ��,���һ���TextView��ʱ����Ȼ�����,���Բ�����ʹ�á�<br>
     * �� http://sunjilife.blog.51cto.com/3430901/1159896 <br>
     * ��ȡ�ؼ��� ��λ px ��
     *
     * @param view
     * @return
     */
    @Deprecated
    public static int getHeightGood(final View view) {
        final int[] height = new int[1];
        // final int[] width = new int[1];
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                height[0] = view.getMeasuredHeight();
                // width[0] = view.getMeasuredWidth();

                System.out.println("height_lv_green_house_Parent " + height[0]);
                return true;
            }
        });
        return (height[0]);
    }

    /**
     * Ŀǰ��˵����õ� ��ȡ�ؼ���ߵķ����ˡ�<br>
     * �� http://sunjilife.blog.51cto.com/3430901/1159896 �� <br>
     * ����֤���ã�<br>
     * <br>
     * <br>
     * <br>
     * ��һ�ֿ��õĻ�ȡ�ؼ���ߵķ�����
     * http://stackoverflow.com/questions/11946424/getmeasuredheight
     * -and-width-returning-0-after-measure ��<br>
     * ֻ�����������onWindowFocusChanged()��Ҳ��ִ�в�ֹһ�Ρ�
     *
     * @param view
     * @return
     */
    public static int getWidthBest(final View view) {
        // final int[] height = new int[1];
        final int[] width = new int[1];

        ViewTreeObserver vto = view.getViewTreeObserver();

        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("NewApi")
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // view.getHeight() ;
                // view.getWidth();

                // view.getMeasuredHeight() ;
                // view.getMeasuredWidth();
            }
        });

        return (width[0]);
    }

    /**
     * �ж�ĳ������(className)�Ƿ���ǰ̨��ʾ��<br>
     * ʹ�ô˷����������������Ȩ�޷���ᱨ����<br>
     * uses-permission android:name="android.permission.GET_TASKS"
     *
     * @param context   ���ڻ�ȡ ActivityManager��������
     * @param className ĳ���������ơ���ȫ���ƣ����� com.android.phone.InCallScreen����
     * @return true ��ʾ ����(className)��ǰ̨��ʾ������false��
     */
    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }

        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }

        return false;
    }

    /**
     * �ж�ĳ��Service�����Ƿ��������С�<br>
     * �ο��� �� http://blog.csdn.net/loongggdroid/article/details/18041147
     *
     * @param mContext    ���ڻ�ȡ ActivityManager��������
     * @param serviceName �ǰ���+�����ȫ���������磺net.loonggg.testbackstage.TestService����
     * @return true�����������У�false�������û���������� ��
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }

        return isWork;
    }

    /**
     * ͨ��su���� ִ��һЩAndroidϵͳ�������� <br>
     * �豸�������ƽ�(���ROOTȨ��). ��Ҳ���豸���������Ȼ�ȡROOTȨ�ޡ���<br>
     *
     * @param cmd
     * @return �����Ƿ�ִ�гɹ���
     */
    public static boolean execSystemCMD(String cmd) {
        Process process = null;
        DataOutputStream os = null;
        boolean flag = false;
        try {
            process = Runtime.getRuntime().exec("su"); // �л���root�ʺ�
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            flag = process.waitFor() == 0;
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return flag;
    }

    // Longʱ���ʽ��
    public static String getYMDHMSFromLong(Long date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        java.util.Date dt = new java.util.Date(date);
        String sDateTime = df.format(dt);
        return sDateTime;
    }

    // Longʱ���ʽ��
    public static String getY_SFromLong(Long date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date dt = new java.util.Date(date);
        String sDateTime = df.format(dt);
        return sDateTime;
    }

    /**
     * Longʱ���ʽ��
     * @param times     �磺1527159637000
     * @param template    �磺yyyy-MM-dd HH:mm:ss
     * @return          2018-05-24 19:00:37
     */
    public static String getDateStrFromLong(long times, String template) {
        if (times < 1 || template == null || ("").equals(template)) {
            return "";
        }
        Date date = new Date(times);
        SimpleDateFormat sdf = new SimpleDateFormat(template, Locale.CHINA);
        return sdf.format(date);
    }

    /**
     * Gets the state of a volume via its mountpoint.<br>
     * note:mountPoint���ص㣨���磬SD�����Ĺ���״̬�ı�ʱ���������̻�ȡ��mountPoint���ص㣨���磬SD�����Ĺ���״̬��
     *
     * @param context
     * @param mountPoint ���ص����·����<br>
     *                   /storage/sdcard��/storage/extsd �ȹ��ص㡣
     */
    public static String getVolumeState(Context context, String mountPoint) {
        String state = Environment.MEDIA_REMOVED;

        StorageManager sm = (StorageManager) context
                .getSystemService(Context.STORAGE_SERVICE);
        try {
            state = (String) StorageManager.class.getMethod("getVolumeState",
                    String.class).invoke(sm, mountPoint);

            Log.i("MSG", mountPoint + "Ŀ¼��״̬  " + state);

        } catch (Exception e) {
            // never reach.
            e.printStackTrace();
        }
        return state;
    }

    /**
     * �����Ƚ���Ӫʱ��
     *
     * @param operaTime �硱06:00-21:00" ����"22:00-06:00" ����
     * @return ����Ӫʱ�����򷵻�true, ���򷵻�false
     */
    public static boolean isOutOfDate(String operaTime) {
        boolean result = false;
        String[] opratorTimes = operaTime.split("--"); //"06:20-21:45"
        if (opratorTimes.length != 2) {
            opratorTimes = operaTime.split("-");
        }
        if (opratorTimes.length != 2) return false;
        Calendar date = Calendar.getInstance();//��ȡ��ǰʱ��
        Calendar startTime = (Calendar) date.clone();
        Calendar endTime = (Calendar) date.clone();

        String[] startTimes = opratorTimes[0].split(":");
        if (startTimes.length != 2) return false;
        startTime.set(Calendar.HOUR_OF_DAY, Integer.valueOf(startTimes[0]));//��һ��ʱ����Ϊ��ǰ8:00
        startTime.set(Calendar.MINUTE, Integer.valueOf(startTimes[1]));
        startTime.set(Calendar.SECOND, 0);

        String[] endTimes = opratorTimes[1].split(":");
        if (endTimes.length != 2) return false;
        endTime.set(Calendar.HOUR_OF_DAY, Integer.valueOf(endTimes[0]));//��һ��ʱ����Ϊ��ǰ8:00
        endTime.set(Calendar.MINUTE, Integer.valueOf(endTimes[1]));
        endTime.set(Calendar.SECOND, 0);

        if (Integer.valueOf(endTimes[0]) < Integer.valueOf(startTimes[0])) {  //������ 22:00-06:00  ҹ�೵
            System.out.println("endHour < startHour");
            startTime.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH) - 1);
        }
        result = (date.after(startTime) && date.before(endTime));//����before��after�ж�
        return result;
    }

    /**
     * ��ȡͼƬ���ԣ���ת�ĽǶ�
     *
     * @param path ͼƬ����·��
     * @return degree��ת�ĽǶ�
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return degree;
        }
        return degree;
    }

    /**
     * ��תͼƬ��ʹͼƬ������ȷ�ķ���
     *
     * @param bitmap  ԭʼͼƬ
     * @param degrees ԭʼͼƬ�ĽǶ�
     * @return Bitmap ��ת���ͼƬ
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        if (degrees == 0 || null == bitmap) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (null != bitmap) {
            bitmap.recycle();
        }
        return bmp;
    }

    /**
     * ���  str �Ƿ�HH:mm��ʽ
     *
     * @param str
     * @return
     */
    public static boolean isValidDate(String str) {
        boolean convertSuccess = true;
        // ָ�����ڸ�ʽΪ��λ��/��λ�·�/��λ���ڣ�ע��yyyy/MM/dd���ִ�Сд��
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        try {
            // ����lenientΪfalse. ����SimpleDateFormat��ȽϿ��ɵ���֤���ڣ�����2007/02/29�ᱻ���ܣ���ת����2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
// ���throw java.text.ParseException����NullPointerException����˵����ʽ����
            convertSuccess = false;
        }
        return convertSuccess;
    }

    // ���ַ���תΪ������
    public static int StrToUInteger(String str) {
        int result = 0;
        try {
            result = Integer.valueOf(str);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(" " + str + " is not unsigned integer ");
        }
        if (result < 0) {
            throw new NumberFormatException(" " + str + " is not unsigned integer ");
        }
        return result;
    }

    // ���ַ���תΪ������
    public static int StrToUInteger(String str, int def) {
        int result = def;
        try {
            result = Integer.valueOf(str);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(" " + str + " is not unsigned integer ");
        }
        if (result < 0) {
            throw new NumberFormatException(" " + str + " is not unsigned integer ");
        }
        return result;
    }

    /**
     * �� string ���� ת���� date
     *
     * @param str
     * @return
     */
    public static Date getDateFormString(String str) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
            // ����lenientΪfalse. ����SimpleDateFormat��ȽϿ��ɵ���֤���ڣ�����2007/02/29�ᱻ���ܣ���ת����2007/03/01
            df.setLenient(false);
            date = df.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }
        return date;
    }

    /**
     * ��������
     *
     * @param context
     * @param intVoice 0-15
     */
    public static void setAudioVoice(Context context, int intVoice) {
        if (intVoice >= 0 && intVoice <= 15) {
//            ZLog.iii("--setAudioVoice--" + intVoice);
            AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, intVoice, 0);
        } else {
//            ZLog.iii("--setAudioVoice--illegality--" + intVoice);
        }
    }

    /**
     * ��ȡ����
     *
     * @param context
     * @return
     */
    public static int getAudioVoice(Context context) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * ��ȡ��ǰʱ��� ����ֵ
     *
     * @param voiceRule "01:00-08:00=2, 08:00-12:00=8, 19:00-20:00=3"
     */
    public static String getAudioVoice(String voiceRule) {
        try {
            String arr[] = voiceRule.split(",");
            if (arr != null && arr.length > 0) {
                for (int i = 0; i < arr.length; i++) {
                    String rules[] = arr[i].split("=");
                    if (rules != null && rules.length > 0) {
                        String times[] = rules[0].split("-");
                        if (times != null && times.length == 2) {
                            String value = rules[1];
                            if (isValidBetwenDate(times[0], times[1])) {
                                // System.out.println(arr[i]);
                                return value;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
//            ZLog.eee("--getAudioVoice--" + voiceRule + "--e=" + e.getLocalizedMessage());
        }
        return "0";
    }

    /**
     * get file md5
     *
     * @param file
     * @return ע���ֶ�ν�һ���ļ����룬���ڴ����ļ����ԣ��Ƚ��Ƽ����ַ�ʽ��ռ���ڴ�Ƚ���
     */
    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bytesToHexString(digest.digest());
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     *  ���ļ���ȡ�ļ�����
     * @param file
     * @return  1= .apk   2 = .png  3 = .jpg 4 = .mp4
     */
    public static int getFileType(File file){
        int fileType = -1;
        if(file != null && file.exists() && file.isFile()){
            String filePath = file.getAbsolutePath();
            int dotIndex = filePath.lastIndexOf(".");
            if(dotIndex<0){
                fileType = -1; //��ֹû����չ�����ļ�
            }
            String filePathSuffix = filePath.substring(dotIndex);
            switch (filePathSuffix){
                case ".apk":
                    fileType = 1;
                    break;
                case ".png":
                    fileType = 2;
                    break;
                case ".jpg":
                    fileType = 3;
                    break;
                case ".mp4":
                    fileType = 4;
                    break;
            }
        }
        return fileType;
    }

    /**
     *  ���ļ����� ��ȡ��׺
     * @param fileType
     * @return  ʮ������ 01 = .apk  02 = .png  03 = .jpg 04 = .mp4
     */
    public static String getFileSuffix(String fileType){
        String fileSuffix = "";
        switch (fileType){
            case "01":
                fileSuffix = ".apk";
                break;
            case "02":
                fileSuffix = ".png";
                break;
            case "03":
                fileSuffix = ".jpg";
                break;
            case "04":
                fileSuffix = ".gif";
                break;
            case "05":
                fileSuffix = ".mp4";
                break;
        }
        return fileSuffix;
    }

    /**
     * �����ַ�������
     *
     * @param time    �磺 System.currentTimeMillis()
     * @param pattern �磺 HH:mm
     * @return �����ַ���ʱ��  HH:mm
     */
    public static String getStringData(long time, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        java.util.Date dt = new java.util.Date(time);
        String sDateTime = df.format(dt);
        return sDateTime;
    }

    /**
     * �� string ���� ת���� long
     *
     * @param str    ��"2018-09-04 14:46:44"
     * @param format �� "yyyy-MM-dd HH:mm:ss"
     * @return
     */
    public static long getLongDateFormString(String str, String format) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(format);
            // ����lenientΪfalse. ����SimpleDateFormat��ȽϿ��ɵ���֤���ڣ�����2007/02/29�ᱻ���ܣ���ת����2007/03/01
            df.setLenient(false);
            Date date = df.parse(str);
            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * �жϵ�ǰʱ�� �Ƿ��� startTime �� endTime ֮��,  �ڷ���true, ���򷵻�false
     *
     * @param startTime 14:46
     * @param endTime   19:46
     * @return boolean
     */
    public static boolean isValidBetwenDate(String startTime, String endTime) {
        if (TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime)) return false;
        long lstartTime = getLongDateFormString(startTime, "HH:mm");
        long lendTime = getLongDateFormString(endTime, "HH:mm");
        long currentTime = getLongDateFormString(getStringData(System.currentTimeMillis(), "HH:mm"), "HH:mm");
        if (currentTime >= lstartTime && currentTime < lendTime) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * �жϵ�ǰʱ�� �Ƿ�  15:27~15:27 ֮��,  �ڷ���true, ���򷵻�false
     *
     * @param time 19:46
     * @return boolean
     */
    public static boolean isValidBetwenDate(String time) {
        try {
            if (TextUtils.isEmpty(time)) return false;
            String str[] = time.split("~");
            if (str.length == 2) {
                String startTime = str[0];
                String endTime = str[1];
                long lstartTime = getLongDateFormString(startTime, "HH:mm");
                long lendTime = getLongDateFormString(endTime, "HH:mm");
                long currentTime = getLongDateFormString(getStringData(System.currentTimeMillis(), "HH:mm"), "HH:mm");
                if (currentTime >= lstartTime && currentTime < lendTime) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * �жϵ�ǰʱ�� �Ƿ��� startTime �� endTime ֮��,  �ڷ���true, ���򷵻�false
     *
     * @param startTime 14:46:30
     * @param endTime   19:46:20
     * @return boolean
     */
    public static boolean isValidBetwenDateHMS(String startTime, String endTime) {
        if (TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime)) return false;
        long lstartTime = getLongDateFormString(startTime, "HH:mm:ss");
        long lendTime = getLongDateFormString(endTime, "HH:mm:ss");
        long currentTime = getLongDateFormString(getStringData(System.currentTimeMillis(), "HH:mm:ss"), "HH:mm:ss");
        if (currentTime >= lstartTime && currentTime < lendTime) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * �жϵ�ǰʱ�� �Ƿ��� startTime �� endTime ֮��,  �ڷ���true, ���򷵻�false
     *
     * @param startTime 2019-06-03
     * @param endTime   2019-06-21
     * @return boolean
     */
    public static boolean isValidBetwenDateYMD(String startTime, String endTime) {
        try {
            if (TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime)) return false;
            long lstartTime = getLongDateFormString(startTime, "yyyy-MM-dd");
            long lendTime = getLongDateFormString(endTime, "yyyy-MM-dd");
            long currentTime = getLongDateFormString(getStringData(System.currentTimeMillis(), "yyyy-MM-dd"), "yyyy-MM-dd");
            if (currentTime >= lstartTime && currentTime <= lendTime) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     *   �ж� time1 �Ƿ�С�� time2
     * @param time1  2019-06-03
     * @param time2  2019-06-21
     * @return    �磺2019-06-03 С�� 2019-06-21 ���� true
     */
    public static boolean isLessThanDateYMD(String time1, String time2){
        try {
            if(time1 == null || time1.equals("") || time2==null || time2.equals("")) return false;
            long ltime1 = getLongDateFormString(time1, "yyyy-MM-dd");
            long ltime2 = getLongDateFormString(time2, "yyyy-MM-dd");
            System.out.println("--isLessThanDateYMD--"+ltime1+"--"+ltime2);
            if (ltime1 < ltime2) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     *  �ж� time1 �Ƿ�С�ڵ�ǰ ʱ��
     * @param time1  2020-07-09
     * @return  ���� ��ǰʱ�� 2020-07-09 17:27:00  time1Ϊ 2020-07-10 �� 2020-07-09,�򷵻�true
     */
    public static boolean isLessThanCurrentDate(String time1){
        try {
            if(time1 == null || time1.equals("")) return false;
            long ltime1 = getLongDateFormString(time1+" 23:59:59", "yyyy-MM-dd HH:mm:ss");
            long ltime2 = System.currentTimeMillis();
            System.out.println("--isLessThanCurrentDate--"+ltime1+"--"+ltime2);
            if (ltime1 < ltime2) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ���  str �Ƿ�HH:mm��ʽ
     *
     * @param str
     * @return
     */
    public static boolean isValidHHmm(String str) {
        if (TextUtils.isEmpty(str)) return false;
        boolean convertSuccess = true;
        // ָ�����ڸ�ʽΪ��λ��/��λ�·�/��λ���ڣ�ע��yyyy/MM/dd���ִ�Сд��
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        try {
            // ����lenientΪfalse. ����SimpleDateFormat��ȽϿ��ɵ���֤���ڣ�����2007/02/29�ᱻ���ܣ���ת����2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
            // ���throw java.text.ParseException����NullPointerException����˵����ʽ����
            convertSuccess = false;
        }
        return convertSuccess;
    }

    /**
     *   ��ʽ�� HH:mm
     * @param str  ��  1:2  ���� 01:02
     * @return
     */
    public static String formatHHmm(String str){
        String result = str;
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        try {
            format.setLenient(false);
            Date date = format.parse(str);
            result = String.format("%tR",date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean isEnglish(String str) {
        if (TextUtils.isEmpty(str)) return false;
//        //����Ӣ�ĺ��������������ַ�(ֻ��Ӣ�����ֵ��ַ���)������true ����false
        return str.matches("[a-zA-Z0-9]+");
    }

    /**
     *  �ж��ַ����Ƿ��� Ӣ����ĸ������
     * @param str
     * @return
     */
    public static boolean hasEnglish(String str){
//        //������Ӣ�ġ�true
        String regex1 = ".*[a-zA-z].*";
        boolean result3 = str.matches(regex1);
//        //���������֡�true
        String regex2 = ".*[0-9].*";
        boolean result4 = str.matches(regex2);
        return result3 || result4;
    }

    /**
     * //�ж��Ƿ�Ϊ�����ģ����Ƿ���false
     *
     * @param str
     * @return
     */
    public static boolean isChinese(String str) {
        if (str==null || str.length() == 0) return false;
//        //��ȫΪӢ�ġ�����true  ����false
//        boolean result1 = str.matches("[a-zA-Z]+");
//        //��ȫΪ���֡�����true
//        Boolean result6 = str.matches("[0-9]+");
//        //����Ӣ�ĺ��������������ַ�(ֻ��Ӣ�����ֵ��ַ���)������true ����false
//        boolean result2 = str.matches("[a-zA-Z0-9]+");
//        //������Ӣ�ġ�true
        String regex1 = ".*[a-zA-z].*";
//        boolean result3 = str.matches(regex1);
//        //���������֡�true
//        String regex2 = ".*[0-9].*";
//        boolean result4 = str.matches(regex2);
        //�ж��Ƿ�Ϊ�����ģ����Ƿ���false
        String regex3 = "[\\u4e00-\\u9fa5]+";
        return str.matches(regex3);
    }

    /**
     * �ж��ַ����Ǻ������ģ����Ƿ���false
     *
     * @param str
     * @return
     */
    public static boolean hasChinese(String str) {
        String regex3 = ".*[\\u4e00-\\u9fa5]+.*";
        return str.matches(regex3);
    }

    /**
     * ��� value �Ƿ� boolean ֵ��
     *
     * @param value
     * @return
     */
    public static boolean isValidBoolean(String value) {
        if (!TextUtils.isEmpty(value)) {
            if (value.equals("true") || value.equals("false")) {
                return true;
            }
        }
        return false;
    }

    /**
     * ��� value �Ƿ�Ϊ int������
     *
     * @param value
     * @return
     */
    public static boolean isValidInt(String value) {
        boolean result = false;
        try {
            if (!TextUtils.isEmpty(value)) {
                Integer.valueOf(value);
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * ���IP:Port �Ƿ�Ϸ� �磺 192.168.1.2:8080
     *
     * @param value
     * @return
     */
    public static boolean isValidIpPort(String value) {
        try {
            if (!TextUtils.isEmpty(value)) {
                String[] str = value.split(":");
                if (str.length == 2) {
                    String ip = str[0];
                    String port = str[1];
                    return checkAddress(ip) && checkPort(port);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * ����Ƿ�Ϊ�Ϸ���ʱ���
     *
     * @param value �磺"14:27~15:27"
     * @return
     */
    public static boolean isValidTimes(String value) {
        if (!TextUtils.isEmpty(value)) {
            String[] str = value.split("~");
            if (str.length == 2) {
                String time1 = str[0];
                String time2 = str[1];
                return isValidHHmm(time1) & isValidHHmm(time2);
            }
        }
        return false;
    }

    /**
     * @param value
     * @return
     */
    public static boolean isValidColor(String value) {
        if (!TextUtils.isEmpty(value)) {
            try {
                Color.parseColor(value);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return false;
    }

    /**
     * @param imgName
     * @return
     */
    public static boolean isValidPic(String imgName) {
        if (!TextUtils.isEmpty(imgName)) {
            if (imgName.endsWith("png") || imgName.endsWith("jpg")) {
                return true;
            }
        }
        return false;
    }

    /**
     * ����Ƿ�Ϊ�Ϸ���ip �� 192.168.1.1
     *
     * @param text
     * @return
     */
    public static boolean ipCheck(String text) {
        if (!TextUtils.isEmpty(text)) {
            // ����������ʽ
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            // �ж�ip��ַ�Ƿ���������ʽƥ��
            if (text.matches(regex)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }


    public static boolean checkAddress(String s) {
        if (TextUtils.isEmpty(s)) return false;
        return s.matches("^(25[0-5]|2[0-4][0-9]|1{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|1{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|1{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|1{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$");
//        return s.matches("((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))");
    }

    public static boolean checkPort(String s) {
        if (TextUtils.isEmpty(s)) return false;
        return s.matches("^6553[0-5]|655[0-2][0-9]|65[0-4][0-9]{2}|6[0-4][0-9]{3}|[1-5][0-9]{4}|[1-9][0-9]{0,3}$");
//        return s.matches("^[1-9]$|(^[1-9][0-9]$)|(^[1-9][0-9][0-9]$)|(^[1-9][0-9][0-9][0-9]$)|(^[1-6][0-5][0-5][0-3][0-5]$)");
    }

    /**
     * �ж�ĳһʱ���Ƿ���һ��������
     *
     * @param sourceTime ʱ������,��պ�,��[10:00-20:00)
     * @param curTime    ��Ҫ�жϵ�ʱ�� ��10:00
     * @return true �ڣ�  false ����
     * @throws // isInTime("16:49-16:43","16:50") ����true
     */
    public static boolean isInTime(String sourceTime, String curTime) {
        if (sourceTime == null || !sourceTime.contains("-") || !sourceTime.contains(":")) {
            throw new IllegalArgumentException("Illegal Argument arg:" + sourceTime);
        }
        if (curTime == null || !curTime.contains(":")) {
            throw new IllegalArgumentException("Illegal Argument arg:" + curTime);
        }
        String[] args = sourceTime.split("-");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            long now = sdf.parse(curTime).getTime();
            long start = sdf.parse(args[0]).getTime();
            long end = sdf.parse(args[1]).getTime();
            if (args[1].equals("00:00")) {
                args[1] = "24:00";
            }
            if (end < start) {
                if (now >= end && now < start) {
                    return false;
                } else {
                    return true;
                }
            } else {
                if (now >= start && now < end) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Illegal Argument arg:" + sourceTime);
        }
    }

    /**
     *  �������������ip
     *  ��isDomainOrIp("192.168.3.3:80")  isDomainOrIp("192.168.3.3")        ����false
     *  isDomainOrIp("www.baidu.com:80")  isDomainOrIp("www.baidu.com")   ����true
     * @param ipPort
     * @return  ���� ����true,  ip����false
     */
    public static boolean isDomainOrIp(String ipPort) {
        try {
            URL url = new URL("http://"+ipPort);
            String host = url.getHost();
            InetAddress address = null;
            address = InetAddress.getByName(host);
            System.out.println(host+"--"+address.getHostAddress());
            if (host.equalsIgnoreCase(address.getHostAddress())){
                System.out.println("ip");
                return false;
            }else{
                System.out.println("domain");
                return true;
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnknownHostException e) {  //����������������ʱ
            e.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @param ip
     * @return   192.168.001.001  ���� 192.168.1.1
     */
    public static String reverseReplenishIP(String ip){
        String []ips = ip.split("\\.");
        if(ips.length == 4){
            for(int i=0; i<ips.length; i++){
                if (ips[i].length() == 3 && ips[i].startsWith("00")) {
                    ips[i] = ips[i].substring(2);
                }else if(ips[i].length() == 3 && ips[i].startsWith("0")) {
                    ips[i] = ips[i].substring(1);
                }
            }
            ip = ips[0]+"."+ips[1]+"."+ips[2]+"."+ips[3];
        }
        return ip;
    }

    /**
     *   ����ip
     * @param ip
     * @return    192.168.1.1 ����192.168.001.001  �����뼯�п�����ͨѶ��
     */
    public static String replenishIP(String ip){
        String []ips = ip.split("\\.");
        if(ips.length == 4){
            for(int i=0; i<ips.length; i++){
                int count = 3 - ips[i].length();
                String newValue = ips[i];
                for(int j=0;  j<count;j++){
                    newValue = "0"+newValue;
                }
                ips[i] = newValue;
            }
            ip = ips[0]+"."+ips[1]+"."+ips[2]+"."+ips[3];
        }
        return ip;
    }

    /**
     * �ṩ��ȷ��С��λ�������봦��
     *
     * @param v
     *            ��Ҫ�������������
     * @param scale
     *            С���������λ
     * @return ���������Ľ��
     *
     *      *     �� v=1.269  scale=2  ���� 1.27
     *      *        v=1      scale=2  ���� 1.0
     */
    public static double round(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The   scale   must   be   a   positive   integer   or   zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * �ṩ��ȷ��С��λ�������봦��
     *
     * @param v
     *            ��Ҫ�������������
     * @param scale
     *            С���������λ
     * @return ����String, ���scale ==0 ʱ����������
     *         double d = 1.22, scale =0 , �򷵻� 1
     *         double dd = 1.2, scale =2 , �򷵻�1.20
     */
    public static String round2(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The   scale   must   be   a   positive   integer   or   zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        double d = b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
        StringBuffer pattern = new StringBuffer("#####0");
        if (scale > 0) {
            pattern.append(".");
            for (int i = 0; i < scale; i++) {
                pattern.append("0");
            }
        }
        DecimalFormat df = new DecimalFormat(pattern.toString());
        return df.format(d);
    }

    public static void main(String[] args) {

        StringBuffer sb = new StringBuffer();
        sb.append("91");    //�豸����      BCD
        sb.append("47");    //���̱�ʶ      BCD
        sb.append("01");    //Ӳ���汾��     BCD
        sb.append("0132");  //����汾��
        sb.append(AscIITools.longToHexString(64098674,8));      //�̼�������ܳ���  UINT32  �ļ��Ĵ�Сʮ������
        sb.append("00000000");      //�����ļ����ۼӺ�n  4�ֽ�
        String apkName = "TM9021-huanwei-0132.apk";
        sb.append(AscIITools.ByteArrToHex(apkName.getBytes()));              //�����ļ�������
        sb.append("0000");
        System.out.println("--update.crl=="+sb.toString());
        byte bb[] = AscIITools.HexToByteArr(sb.toString());
        try{
            String result = new String(AscIITools.HexToByteArr(sb.toString()),"GBK");
            System.out.println("--main--str=="+result);
        }catch (Exception e){
            System.out.println("--main--str==e="+e.getLocalizedMessage());
        }

        System.out.println("--main--");
        for(byte b : bb){
            System.out.print(b);
        }
        System.out.println("--main--end--");

        try{
            FileInputStream fis = new FileInputStream("E:\\1.crl");
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509CRL aCrl = (X509CRL) cf.generateCRL(fis);

        }catch (Exception e){

        }




//        System.out.println("add="+checkAddress("1.168.1.1"));
//        System.out.println("port="+checkPort("65536"));
//        System.out.println("int="+isValidInt("null"));
//        System.out.println("time="+isValidTimes("12:00~13:00"));
//        System.out.println("ipport="+isValidIpPort("218.28.140.212:13714"));
//        System.out.println("ipport="+checkPort("13714"));
//        String id = "192.168:1.1:1";
//        System.out.println("port="+id.substring(id.lastIndexOf(":")+1));

//        String hexStr = "24 24 00 15 7B 00 01 00 18 E7 F8 89 8C 8F 92 FF FF FF FF "; // 08 4F
//        String hexStr = "24 24 00 2D 7B 00 04 00 18 E7 F8 00 04 00 38 00 02 00 72 00 00 00 00 00 00 00 00 FF FF FF FF 00 3B 00 00 00 00 00 00 00 00 00 00 E5 D9";
//        hexStr = hexStr.replace(" ", "");
//        String crcResult = CRC16Util.calcCrc16LH(AscIITools.HexToByteArr(hexStr.substring(0,hexStr.length()-4)));
//
//        int result  = CRC16Util.calcCrc16(AscIITools.HexToByteArr(hexStr),0,AscIITools.HexToByteArr(hexStr).length, 0xffff);
//
//        System.out.println(crcResult+"--"+hexStr+"--"+result +"--"+CRC16Util.calcCrc16(AscIITools.HexToByteArr(hexStr)));

//        String str1 = "X101";
//        System.out.println("--main1--"+isEnglish(str1));
//        String str = "��E9";
//        System.out.println("--main--"+isChinese(str)+"--"+hasChinese(str));

//        String plan = "06:31:42~10:31:42#10:40:50~10:31:42#10:59:30~15:31:42";
//        System.out.println(isInPlan(plan));

//        System.out.println(isValidBetwenDateYMD("2018-06-11", "2019-06-10"));

//        System.out.println(isInTime("19:46-19:45","08:03"));

//        String ip = "192.168.10.11";
//        String []ips = ip.split("\\.");
//        System.out.println(ips[0]+"--"+ips[1]+"--"+ips[2]+"--"+ips[3]);
//        System.out.println(replenishIP(ip));
//        ips = ip.split("\\.");
//        System.out.println(ips[0]+"--"+ips[1]+"--"+ips[2]+"--"+ips[3]);
//        System.out.println(AscIITools.ByteArrToHex(AppUtils.replenishIP(replenishIP(ip)).getBytes()));
//        System.out.println(reverseReplenishIP("192.168.101.212"));
//        System.out.println(reverseReplenishIP("192.168.011.012"));
//        System.out.println(reverseReplenishIP("192.168.001.002"));
//        System.out.println(reverseReplenishIP("192.168.100.002"));
//        System.out.println(reverseReplenishIP("192.168.100.020"));
//        System.out.println(reverseReplenishIP("192.168.90.200"));


//        30 30 4b 30 30 30 30 30 38 39 38 39 30 31 30 30 31 36 37 38 39
//        ˾�����ţ�89890   ���ţ�16789  ���ƣ�21���ֽ�
//        order=F==24263030323230304630303138313035313030313133343030CCA9BBAABEC6B5EA23BBF0B3B5D5BE463042360D
//        2426 30303232 3030 46 30303138 313035313030 313133343030 CCA9BBAABEC6B5EA23BBF0B3B5D5BE 46304236 0D
        String hex = "24263030323230304630303138313035313030313133343030CCA9BBAABEC6B5EA23BBF0B3B5D5BE463042360D";
        System.out.println(AscIITools.HexToInt("30303232"));
        try{
            System.out.println(new String(AscIITools.HexStr2Bytes("CCA9BBAABEC6B5EA23BBF0B3B5D5BE"),"GBK"));

            Byte len1 = AscIITools.HexToByte("30");
            Byte len2 = AscIITools.HexToByte("30");
            Byte len3 = AscIITools.HexToByte("31");
            Byte len4 = AscIITools.HexToByte("35");
            String len1234 = "" + (char) len1.byteValue() + ""
                    + (char) len2.byteValue() + ""
                    + (char) len3.byteValue() + ""
                    + (char) len4.byteValue();
            System.out.println("--len1234--"+len1234);
            int LLLLInt = Integer.parseInt(len1234, 16);
            System.out.println(LLLLInt);
            System.out.println(AscIITools.HexToInt(new String(AscIITools.HexToByteArr("30303135"))));

//            String testHex = "30303232 3030 46 30303138 313035313030 313133343030 CCA9BBAABEC6B5EA23BBF0B3B5D5BE"; // 46304236 == F0B6
            String testHex = "30303135  3030 4b 30303030303839383930  3130303136373839"; //0513 = FAEC
            String hexStr = new String(AscIITools.HexToByteArr(testHex.replace(" ","")),"GBK");
            String testHex2 = AscIITools.getSumCheck(testHex.replace(" ",""),2);

            System.out.println("-HexToAscii-"+AscIITools.HexStrToAscii("FAEC"));
//            System.out.println("-AsciiStrToHex-"+AscIITools.AsciiStrToHex("46304236"));
        }catch (Exception e){
            e.printStackTrace();
        }

//     2426 30303135  3030 4b 30303030303839383930  3130303136373839  46414543

        try{
            //        2426 30303343 30304232303031313730C7B0B7BDB5BDD5BED3C0D0CBC2B7CBB3C6BDBDD6BFDA202020202020C7B0B7BDB5BDD5BED3C0D0CBC2B7CBB3C6BDBDD6BFDA444145340D
            String hyHex = "2426 30303135  3030 4b 30303030303839383930  3130303136373839  46414543";
            hyHex = hyHex.replace(" ", "");

            byte[] hybytes = AscIITools.HexStr2Bytes(hyHex);
//            System.out.println("hyHex=="+byte2hex(hybytes));

            byte[] di = new byte[10];
            System.arraycopy(hybytes,9,di,0,10);
            String diStr = (new String(di,"GBK"));
//            System.out.println("di=="+diStr+"--"+delPrefix(diStr));

            byte type = hybytes[19];
//            System.out.println("--type--"+type+"--"+(type == 49)+"--");

            byte[] vi = new byte[7];
            System.arraycopy(hybytes,20,vi,0,7);
            String viStr = (new String(vi,"GBK"));
//            System.out.println("vi=="+viStr+"--"+delPrefix(viStr));

            String driverInfo = hyHex.substring(9*2,19*2); //˾����Ϣ
            String vehice = hyHex.substring(19*2,27*2); //������Ϣ
            String realVehice = hyHex.substring(20*2,27*2); //������Ϣ
//        System.out.println("driverInfo="+driverInfo+"--vehice="+vehice);
//        System.out.println("driverInfo="+AscIITools.AsciiStrToHex(driverInfo)+"--vehice="+AscIITools.AsciiStrToHex(realVehice));
//            System.out.println("delPrefix="+delPrefix("010001"));

//            System.out.println("--hasEnglish--"+hasEnglish("2·������վ"));
//            System.out.println("--hasEnglish--"+hasEnglish("Y·������վ"));
//            System.out.println("--hasEnglish--"+hasEnglish("Y1·������վ"));
//            System.out.println("--hasEnglish--"+hasEnglish("Y1123"));
//            System.out.println("--hasEnglish--"+hasEnglish("·������վ"));
//            System.out.println("--hasEnglish--"+isChinese("·������վ"));

//            System.out.println("--isLessThanDateYMD--"+isLessThanDateYMD("2020-07-01","2020-07-09"));
            System.out.println("--isLessThanCurrentDate--"+isLessThanCurrentDate("2020-07-09"));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static String delPrefix(String str){
        String result = "";
        for(int i=0; i<str.length(); i++){
            if(!"0".equals(str.substring(i,i+1))){
                result = str.substring(i);
                break;
            }
        }
        return result;
    }
    public static final String byte2hex(byte b[]) {
        if (b == null) {
            throw new IllegalArgumentException(
                    "Argument b ( byte array ) is null! ");
        }
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xff);
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs.toUpperCase();
    }

    /**
     * �ַ���ת��ΪAscii
     * @param value
     * @return
     */
    public static String stringToAscii(String value){
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if(i != chars.length - 1){
                sbu.append((int)chars[i]).append(",");
            }else {
                sbu.append((int)chars[i]);
            }
        }
        return sbu.toString();
    }

    /**
     * Asciiת��Ϊ�ַ���
     * @param value
     * @return
     */
    public static String asciiToString(String value){
        StringBuffer sbu = new StringBuffer();
        String[] chars = value.split(",");
        for (int i = 0; i < chars.length; i++) {
            sbu.append((char) Integer.parseInt(chars[i]));
        }
        return sbu.toString();
    }

    public static void setPowerOnOff(Context context, String powerOffTime, String powerOnTime){
        //���ö�ʱ���ػ�
        SmdtManager mSMDT = SmdtManager.create(context);
        if (AppUtils.isValidHHmm(powerOffTime)
                && AppUtils.isValidHHmm(powerOnTime)
                && DateUtils.getTwoHour(powerOffTime, powerOnTime)) {
            ZLog.iii("--smdtSetTimingSwitchMachine--offTime="+powerOffTime+"--onTime="+powerOnTime);
            mSMDT.smdtSetTimingSwitchMachine(powerOffTime, powerOnTime, "1");
        }
    }
}
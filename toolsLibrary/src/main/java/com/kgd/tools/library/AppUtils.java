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
 * 跟App/apk 相关的辅助类
 *
 * getAppName  获取应用程序名称
 * getVersionName 当前应用的版本名称
 * getVersionCode 当前应用的版本号
 * isInstallSoftware 检测 packageName 是否已经安装
 * installSlient
 * unInstallSlient
 * installApk   安装 Apk
 * uninstallApk 卸载 Apk
 * isServiceRunning  Service是否正在运行
 * stopRunningService 停止service
 * getCPUNumCores  查看CPU核心数
 * importDatabase  导入数据库
 * setImmersiveMode 沉浸模式
 * getStatusBarHeight 状态栏高度
 * closeSoftInput 隐藏软键盘
 * getEth0Mac   获取 eth0  mac地址
 * getWifiMac   获取 wlan  mac地址
 * <p>
 * getIMSI(Context context) 手机卡的IMSI    如：IMSI=460031304158115
 * getIMEI(Context context) 获取手机的IMEI    如：IMEI=866946025546414
 * isOutOfDate(String operaTime)    用来比较运营时间，判断当前时间是否超出 operatiome(06:00-21:00)范围
 * readPictureDegree(String path)   读取图片属性：旋转的角度
 * rotateBitmap(Bitmap bitmap, int degrees)  旋转图片
 * boolean hasChinese(String str)   判断字符串是含有中文，不是返回false
 */
public class AppUtils
{

    private AppUtils(){
        /**cannot be instantiated **/
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 获取应用程序名称
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
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
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
     * [获取当前应用的版本号]
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
     * 检测 packageName 是否已经安装
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
     * install slient。<br>
     * 最好在新建的线程中运行并通过handler发送安装结果给主线程，否则安装时间较长会导致ANR。<br>
     * 静默安装条件：<br>
     * 1、需要把当前方法（也即installSlient方法）所在的apk做成系统应用。就是系统root过后把文件放到/system/app目录下。/
     * system/app目录下的apk都是系统应用。<br>
     * 2、需要在AndroidManifest.xml文件中注册 android.permission.INSTALL_PACKAGES 权限。
     *
     * @param filePath 要安装的apk的路径。
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
     *  强制 阶级安装
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
            } else if (Build.VERSION.SDK_INT >= 19) {  //Android 4.4 之后
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
     * uninstall slient。<br>
     * 最好在新建的线程中运行并通过handler发送安装结果给主线程，否则安装时间较长会导致ANR。<br>
     * 静默卸载条件：<br>
     * 1、需要系统应用。就是系统root过后把文件放到/system/app目录下。/
     * system/app目录下的apk都是系统应用。<br>
     * 2、需要在AndroidManifest.xml文件中注册 android.permission.INSTALL_PACKAGES 权限。
     *
     * @param packageName 卸载的包名
     * @return 0 means normal, 1 means file not exist, 2 means other exceptionerror
     */
    public static int unInstallSlient(String packageName) {
        ShellUtils.CommandResult result = ShellUtils.execCommand("pm uninstall " + packageName, true);
//        ZLog.iii("--unInstallSlient--" + packageName + "--result=" + result.successMsg + "--" + result.errorMsg);
        return result.result;
    }

    /**
     * 描述：安装apk.
     *
     * @param context the context
     * @param file apk文件路径
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
     * 描述：卸载程序.
     *
     * @param context the context
     * @param packageName 包名
     */
    public static void uninstallApk(Context context,String packageName) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        Uri packageURI = Uri.parse("package:" + packageName);
        intent.setData(packageURI);
        context.startActivity(intent);
    }

    /**
     *  检测apk文件并安装
     *  1. 检测文件完整性
     *  2. 检测包名是否一致
     *  3. 检测版本号是比正在运行的大
     * @param apkPath   文件路径
     * @param context
     * @param isForce   是否强制安装 即使当前文件版本号小于 正在运行的版本号
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
     * Service是否正在运行
     *
     * @param ctx
     * @param className 判断的服务名字 "com.xxx.xx..XXXService"
     * @return true在运行    false不在运行
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
     * 停止服务.
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
     * 获取CPU核心数
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
     * 状态栏高度
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
     * 关闭键盘事件.
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

        /**隐藏软键盘**/
        View view = ((Activity) context).getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    /**
     * install slient。<br>
     * 最好在新建的线程中运行并通过handler发送安装结果给主线程，否则安装时间较长会导致ANR。<br>
     * 静默安装条件：<br>
     * 1、需要把当前方法（也即installSlient方法）所在的apk做成系统应用。就是系统root过后把文件放到/system/app目录下。/
     * system/app目录下的apk都是系统应用。<br>
     * 2、需要在AndroidManifest.xml文件中注册 android.permission.INSTALL_PACKAGES 权限。
     *
     * @param filePath 要安装的apk的路径。
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
     * 手机卡的IMSI    如：IMSI=460031304158115
     *
     * @param context
     * @return
     */
    public static String getIMSI(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getSubscriberId();
    }

    /**
     * 获取手机的IMEI    如：IMEI=866946025546414
     *
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getDeviceId();
    }

    /**
     * 检测当的网络（WLAN、3G/2G）状态
     *
     * @param context Context
     * @return true 表示网络可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED &&
                        info.isAvailable()) {
                    if (ping()) {
                        return true;    // 当前所连接的网络可用
                    }else{
                        return false;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断是否有外网连接（普通方法不能判断外网的网络是否连接，比如连接上局域网）
     *
     * @return 必须放在线程里执行，否则会遇到anr
     */
    public static final boolean ping() {
        String result = null;
        try {
            String ip = "www.baidu.com";// ping 的地址，可以换成任何一种可靠的外网
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping网址3次
            // 读取ping的内容，可以不加
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer stringBuffer = new StringBuffer();
            String content = "";
            while ((content = in.readLine()) != null) {
                stringBuffer.append(content);
            }
            Thread.sleep(200);
            // ping的状态
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
     * 获得以太网的Mac地址
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
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        macSerial = macSerial.replace(":", "");
        return macSerial;
    }

    /**
     * 获得wifi mac地址
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
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return macSerial;
    }

    /**
     * 设置手机移动数据开关  Androd 4.2 新测可以
     *
     * @param pContext
     * @param pBoolean ture 开启移动数据，false 关闭移动数据
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
     * 返回手机移动数据的状态
     *
     * @param pContext
     * @param arg      默认填null
     * @return true 连接 false 未连接
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
     * 新增一个APN接入点
     * APN：umim2m.njm2mapn或者umim2m.gzm2mapn
     * MCC：460
     * MNC：06
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

        values.put("name", apnName); // apn中文描述
        values.put("apn", apn); // apn名称
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
     * 罗列出所有的APN（默认的有很多APN,使用时最好根据apn进行过滤）
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
     * 设置默认的接入点
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
     * 检查是否存在SD卡
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
     * 转换dip为px，保证尺寸大小不变。 <br>
     * Resources对象的getDimension()方法其实与dip2px()方法是等价的， 都可以把一个dip转化为px。<br>
     * 比如，<br>
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
     * 转换px为dip，保证尺寸大小不变。
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
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取屏幕高的方法。返回值为px 。
     *
     * @param context
     */
    public static int getScreenHeight(Context context) {
        // 第一种
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        // 屏幕宽（像素，如：480px）
        int width = display.getWidth();
        // 屏幕高（像素，如：800px）
        int height = display.getHeight();
        Log.d("width", String.valueOf(width));
        Log.d("height", String.valueOf(height));

        // 第二种
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);

        int height2 = dm.heightPixels;

        return height2;
    }

    /**
     * 获取屏幕宽的方法。
     *
     * @param context
     */
    public static int getScreenWidth(Context context) {
        // 第一种
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        // 屏幕宽（像素，如：480px）
        int width = display.getWidth();
        // 屏幕高（像素，如：800px）
        int height = display.getHeight();
        Log.d("width", String.valueOf(width));
        Log.d("height", String.valueOf(height));

        // 第二种
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        int width2 = dm.widthPixels;

        return width2;
    }

    /**
     * 这种方法并不是适合所有场景。<br>
     * 详见：https://zhidao.baidu.com/question/2266663031632027308.html <br>
     * <br>
     *
     * @param view
     * @return 获取控件宽 单位 px 。
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
     * 这种方法并不是适合所有场景。<br>
     * 详见：https://zhidao.baidu.com/question/2266663031632027308.html <br>
     * <br>
     *
     * @param view
     * @return 获取控件高 单位 px 。
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
     * 这种方法行是行，但是不建议使用。<br>
     * 因为这个回调方法会调用很多次,并且滑动TextView的时候仍然会调用,所以不建议使用。<br>
     * 见 http://sunjilife.blog.51cto.com/3430901/1159896 <br>
     * 获取控件宽 单位 px 。
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
     * 这种方法行是行，但是不建议使用。<br>
     * 因为这个回调方法会调用很多次,并且滑动TextView的时候仍然会调用,所以不建议使用。<br>
     * 见 http://sunjilife.blog.51cto.com/3430901/1159896 <br>
     * 获取控件高 单位 px 。
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
     * 目前来说是最好的 获取控件宽高的方法了。<br>
     * 见 http://sunjilife.blog.51cto.com/3430901/1159896 。 <br>
     * 经验证可用！<br>
     * <br>
     * <br>
     * <br>
     * 另一种可用的获取控件宽高的方法见
     * http://stackoverflow.com/questions/11946424/getmeasuredheight
     * -and-width-returning-0-after-measure 。<br>
     * 只是这个方法（onWindowFocusChanged()）也会执行不止一次。
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
     * 判断某个界面(className)是否在前台显示。<br>
     * 使用此方法，必须加上以下权限否则会报错。。<br>
     * uses-permission android:name="android.permission.GET_TASKS"
     *
     * @param context   用于获取 ActivityManager管理器。
     * @param className 某个界面名称。（全名称，比如 com.android.phone.InCallScreen。）
     * @return true 表示 界面(className)在前台显示，否则false。
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
     * 判断某个Service服务是否正在运行。<br>
     * 参考自 ： http://blog.csdn.net/loongggdroid/article/details/18041147
     *
     * @param mContext    用于获取 ActivityManager管理器。
     * @param serviceName 是包名+服务的全类名（例如：net.loonggg.testbackstage.TestService）。
     * @return true代表正在运行，false代表服务没有正在运行 。
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
     * 通过su命令 执行一些Android系统级别的命令。 <br>
     * 设备必须已破解(获得ROOT权限). （也即设备必须已事先获取ROOT权限。）<br>
     *
     * @param cmd
     * @return 命令是否执行成功。
     */
    public static boolean execSystemCMD(String cmd) {
        Process process = null;
        DataOutputStream os = null;
        boolean flag = false;
        try {
            process = Runtime.getRuntime().exec("su"); // 切换到root帐号
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

    // Long时间格式化
    public static String getYMDHMSFromLong(Long date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        java.util.Date dt = new java.util.Date(date);
        String sDateTime = df.format(dt);
        return sDateTime;
    }

    // Long时间格式化
    public static String getY_SFromLong(Long date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date dt = new java.util.Date(date);
        String sDateTime = df.format(dt);
        return sDateTime;
    }

    /**
     * Long时间格式化
     * @param times     如：1527159637000
     * @param template    如：yyyy-MM-dd HH:mm:ss
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
     * note:mountPoint挂载点（比如，SD卡）的挂载状态改变时，不能立刻获取到mountPoint挂载点（比如，SD卡）的挂载状态。
     *
     * @param context
     * @param mountPoint 挂载点绝对路径。<br>
     *                   /storage/sdcard、/storage/extsd 等挂载点。
     */
    public static String getVolumeState(Context context, String mountPoint) {
        String state = Environment.MEDIA_REMOVED;

        StorageManager sm = (StorageManager) context
                .getSystemService(Context.STORAGE_SERVICE);
        try {
            state = (String) StorageManager.class.getMethod("getVolumeState",
                    String.class).invoke(sm, mountPoint);

            Log.i("MSG", mountPoint + "目录的状态  " + state);

        } catch (Exception e) {
            // never reach.
            e.printStackTrace();
        }
        return state;
    }

    /**
     * 用来比较运营时间
     *
     * @param operaTime 如”06:00-21:00" 或者"22:00-06:00" 这种
     * @return 在运营时间内则返回true, 否则返回false
     */
    public static boolean isOutOfDate(String operaTime) {
        boolean result = false;
        String[] opratorTimes = operaTime.split("--"); //"06:20-21:45"
        if (opratorTimes.length != 2) {
            opratorTimes = operaTime.split("-");
        }
        if (opratorTimes.length != 2) return false;
        Calendar date = Calendar.getInstance();//获取当前时间
        Calendar startTime = (Calendar) date.clone();
        Calendar endTime = (Calendar) date.clone();

        String[] startTimes = opratorTimes[0].split(":");
        if (startTimes.length != 2) return false;
        startTime.set(Calendar.HOUR_OF_DAY, Integer.valueOf(startTimes[0]));//将一个时间设为当前8:00
        startTime.set(Calendar.MINUTE, Integer.valueOf(startTimes[1]));
        startTime.set(Calendar.SECOND, 0);

        String[] endTimes = opratorTimes[1].split(":");
        if (endTimes.length != 2) return false;
        endTime.set(Calendar.HOUR_OF_DAY, Integer.valueOf(endTimes[0]));//将一个时间设为当前8:00
        endTime.set(Calendar.MINUTE, Integer.valueOf(endTimes[1]));
        endTime.set(Calendar.SECOND, 0);

        if (Integer.valueOf(endTimes[0]) < Integer.valueOf(startTimes[0])) {  //适用于 22:00-06:00  夜班车
            System.out.println("endHour < startHour");
            startTime.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH) - 1);
        }
        result = (date.after(startTime) && date.before(endTime));//利用before和after判断
        return result;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
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
     * 旋转图片，使图片保持正确的方向。
     *
     * @param bitmap  原始图片
     * @param degrees 原始图片的角度
     * @return Bitmap 旋转后的图片
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
     * 检查  str 是否HH:mm格式
     *
     * @param str
     * @return
     */
    public static boolean isValidDate(String str) {
        boolean convertSuccess = true;
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess = false;
        }
        return convertSuccess;
    }

    // 将字符串转为正整形
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

    // 将字符串转为正整形
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
     * 将 string 日期 转化成 date
     *
     * @param str
     * @return
     */
    public static Date getDateFormString(String str) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            df.setLenient(false);
            date = df.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }
        return date;
    }

    /**
     * 设置音量
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
     * 获取音量
     *
     * @param context
     * @return
     */
    public static int getAudioVoice(Context context) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 获取当前时间段 音量值
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
     * @return 注：分多次将一个文件读入，对于大型文件而言，比较推荐这种方式，占用内存比较少
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
     *  由文件获取文件类型
     * @param file
     * @return  1= .apk   2 = .png  3 = .jpg 4 = .mp4
     */
    public static int getFileType(File file){
        int fileType = -1;
        if(file != null && file.exists() && file.isFile()){
            String filePath = file.getAbsolutePath();
            int dotIndex = filePath.lastIndexOf(".");
            if(dotIndex<0){
                fileType = -1; //防止没有扩展名的文件
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
     *  由文件类型 获取后缀
     * @param fileType
     * @return  十六进制 01 = .apk  02 = .png  03 = .jpg 04 = .mp4
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
     * 返回字符串日期
     *
     * @param time    如： System.currentTimeMillis()
     * @param pattern 如： HH:mm
     * @return 返回字符串时间  HH:mm
     */
    public static String getStringData(long time, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        java.util.Date dt = new java.util.Date(time);
        String sDateTime = df.format(dt);
        return sDateTime;
    }

    /**
     * 将 string 日期 转化成 long
     *
     * @param str    如"2018-09-04 14:46:44"
     * @param format 如 "yyyy-MM-dd HH:mm:ss"
     * @return
     */
    public static long getLongDateFormString(String str, String format) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(format);
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            df.setLenient(false);
            Date date = df.parse(str);
            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 判断当前时间 是否在 startTime 与 endTime 之间,  在返回true, 否则返回false
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
     * 判断当前时间 是否  15:27~15:27 之间,  在返回true, 否则返回false
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
     * 判断当前时间 是否在 startTime 与 endTime 之间,  在返回true, 否则返回false
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
     * 判断当前时间 是否在 startTime 与 endTime 之间,  在返回true, 否则返回false
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
     *   判断 time1 是否小于 time2
     * @param time1  2019-06-03
     * @param time2  2019-06-21
     * @return    如：2019-06-03 小于 2019-06-21 返回 true
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
     *  判断 time1 是否小于当前 时间
     * @param time1  2020-07-09
     * @return  比如 当前时间 2020-07-09 17:27:00  time1为 2020-07-10 或 2020-07-09,则返回true
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
     * 检查  str 是否HH:mm格式
     *
     * @param str
     * @return
     */
    public static boolean isValidHHmm(String str) {
        if (TextUtils.isEmpty(str)) return false;
        boolean convertSuccess = true;
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess = false;
        }
        return convertSuccess;
    }

    /**
     *   格式化 HH:mm
     * @param str  如  1:2  返回 01:02
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
//        //【除英文和数字外无其他字符(只有英文数字的字符串)】返回true 否则false
        return str.matches("[a-zA-Z0-9]+");
    }

    /**
     *  判断字符串是否含有 英文字母或数字
     * @param str
     * @return
     */
    public static boolean hasEnglish(String str){
//        //【含有英文】true
        String regex1 = ".*[a-zA-z].*";
        boolean result3 = str.matches(regex1);
//        //【含有数字】true
        String regex2 = ".*[0-9].*";
        boolean result4 = str.matches(regex2);
        return result3 || result4;
    }

    /**
     * //判断是否为纯中文，不是返回false
     *
     * @param str
     * @return
     */
    public static boolean isChinese(String str) {
        if (str==null || str.length() == 0) return false;
//        //【全为英文】返回true  否则false
//        boolean result1 = str.matches("[a-zA-Z]+");
//        //【全为数字】返回true
//        Boolean result6 = str.matches("[0-9]+");
//        //【除英文和数字外无其他字符(只有英文数字的字符串)】返回true 否则false
//        boolean result2 = str.matches("[a-zA-Z0-9]+");
//        //【含有英文】true
        String regex1 = ".*[a-zA-z].*";
//        boolean result3 = str.matches(regex1);
//        //【含有数字】true
//        String regex2 = ".*[0-9].*";
//        boolean result4 = str.matches(regex2);
        //判断是否为纯中文，不是返回false
        String regex3 = "[\\u4e00-\\u9fa5]+";
        return str.matches(regex3);
    }

    /**
     * 判断字符串是含有中文，不是返回false
     *
     * @param str
     * @return
     */
    public static boolean hasChinese(String str) {
        String regex3 = ".*[\\u4e00-\\u9fa5]+.*";
        return str.matches(regex3);
    }

    /**
     * 检测 value 是否 boolean 值，
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
     * 检测 value 是否为 int型数据
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
     * 检查IP:Port 是否合法 如： 192.168.1.2:8080
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
     * 检测是否为合法的时间段
     *
     * @param value 如："14:27~15:27"
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
     * 检查是否为合法的ip 如 192.168.1.1
     *
     * @param text
     * @return
     */
    public static boolean ipCheck(String text) {
        if (!TextUtils.isEmpty(text)) {
            // 定义正则表达式
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            // 判断ip地址是否与正则表达式匹配
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
     * 判断某一时间是否在一个区间内
     *
     * @param sourceTime 时间区间,半闭合,如[10:00-20:00)
     * @param curTime    需要判断的时间 如10:00
     * @return true 在；  false 不在
     * @throws // isInTime("16:49-16:43","16:50") 返回true
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
     *  检测是域名还是ip
     *  如isDomainOrIp("192.168.3.3:80")  isDomainOrIp("192.168.3.3")        返回false
     *  isDomainOrIp("www.baidu.com:80")  isDomainOrIp("www.baidu.com")   返回true
     * @param ipPort
     * @return  域名 返回true,  ip返回false
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
        } catch (UnknownHostException e) {  //无网络或解析不出来时
            e.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @param ip
     * @return   192.168.001.001  返回 192.168.1.1
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
     *   补充ip
     * @param ip
     * @return    192.168.1.1 返回192.168.001.001  用于与集中控制器通讯用
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
     * 提供精确的小数位四舍五入处理。
     *
     * @param v
     *            需要四舍五入的数字
     * @param scale
     *            小数点后保留几位
     * @return 四舍五入后的结果
     *
     *      *     如 v=1.269  scale=2  返回 1.27
     *      *        v=1      scale=2  返回 1.0
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
     * 提供精确的小数位四舍五入处理。
     *
     * @param v
     *            需要四舍五入的数字
     * @param scale
     *            小数点后保留几位
     * @return 返回String, 针对scale ==0 时，返回整数
     *         double d = 1.22, scale =0 , 则返回 1
     *         double dd = 1.2, scale =2 , 则返回1.20
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
        sb.append("91");    //设备类型      BCD
        sb.append("47");    //厂商标识      BCD
        sb.append("01");    //硬件版本号     BCD
        sb.append("0132");  //软件版本号
        sb.append(AscIITools.longToHexString(64098674,8));      //固件代码的总长度  UINT32  文件的大小十六进制
        sb.append("00000000");      //整个文件的累加和n  4字节
        String apkName = "TM9021-huanwei-0132.apk";
        sb.append(AscIITools.ByteArrToHex(apkName.getBytes()));              //程序文件的名称
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
//        String str = "定E9";
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
//        司机卡号：89890   车号：16789  共计：21个字节
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

            String driverInfo = hyHex.substring(9*2,19*2); //司机信息
            String vehice = hyHex.substring(19*2,27*2); //其他信息
            String realVehice = hyHex.substring(20*2,27*2); //其他信息
//        System.out.println("driverInfo="+driverInfo+"--vehice="+vehice);
//        System.out.println("driverInfo="+AscIITools.AsciiStrToHex(driverInfo)+"--vehice="+AscIITools.AsciiStrToHex(realVehice));
//            System.out.println("delPrefix="+delPrefix("010001"));

//            System.out.println("--hasEnglish--"+hasEnglish("2路客运南站"));
//            System.out.println("--hasEnglish--"+hasEnglish("Y路客运南站"));
//            System.out.println("--hasEnglish--"+hasEnglish("Y1路客运南站"));
//            System.out.println("--hasEnglish--"+hasEnglish("Y1123"));
//            System.out.println("--hasEnglish--"+hasEnglish("路客运南站"));
//            System.out.println("--hasEnglish--"+isChinese("路客运南站"));

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
     * 字符串转换为Ascii
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
     * Ascii转换为字符串
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
        //设置定时开关机
        SmdtManager mSMDT = SmdtManager.create(context);
        if (AppUtils.isValidHHmm(powerOffTime)
                && AppUtils.isValidHHmm(powerOnTime)
                && DateUtils.getTwoHour(powerOffTime, powerOnTime)) {
            ZLog.iii("--smdtSetTimingSwitchMachine--offTime="+powerOffTime+"--onTime="+powerOnTime);
            mSMDT.smdtSetTimingSwitchMachine(powerOffTime, powerOnTime, "1");
        }
    }
}
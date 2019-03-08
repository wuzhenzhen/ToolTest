package com.kgd.tools.library;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

//import com.lazy.library.logging.Logcat;
//import com.tiamaes.sectionstationscreen4gj.application.SSSApplication;


/**
 * Created by wzz on 2016/08/17.
 * wuzhenzhen@tiamaes.com
 *
 * 跟App/apk 相关的辅助类
 *
 * getAppName  获取应用程序名称
 * getVersionName 当前应用的版本名称
 * getVersionCode 当前应用的版本号
 * installApk   安装 Apk
 * uninstallApk 卸载 Apk
 * isServiceRunning  Service是否正在运行
 * stopRunningService 停止service
 * getCPUNumCores  查看CPU核心数
 * importDatabase  导入数据库
 * setImmersiveMode 沉浸模式
 * getStatusBarHeight 状态栏高度
 * closeSoftInput 隐藏软键盘
 *
 * getIMSI(Context context) 手机卡的IMSI    如：IMSI=460031304158115
 * getIMEI(Context context) 获取手机的IMEI    如：IMEI=866946025546414
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
     *  Service是否正在运行
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
     * @return The number of cores, or 1 if failed to get result
     */
    public static int getCPUNumCores() {
        try {
            //Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            //Filter to only list the devices we care about
            File[] files = dir.listFiles(new FileFilter(){

                @Override
                public boolean accept(File pathname) {
                    //Check if filename is "cpu", followed by a single digit number
                    if(Pattern.matches("cpu[0-9]", pathname.getName())) {
                        return true;
                    }
                    return false;
                }
            });
            //Return the number of cores (virtual CPU devices)
            return files.length;
        } catch(Exception e) {
            //Default to return 1 core
            return 1;
        }
    }

    /**
     *  状态栏高度
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
        View view = ((Activity)context).getWindow().peekDecorView();
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
     * @param filePath
     *            要安装的apk的路径。
     * @return 0 means normal, 1 means file not exist, 2 means other exception
     *         error
     */
    public static int installSlient(String filePath) {
        File file = new File(filePath);
        if (filePath == null || filePath.length() == 0
                || (file = new File(filePath)) == null || file.length() <= 0
                || !file.exists() || !file.isFile()) {

            return 1;
        }

        String[] args = { "pm", "install", "-r", filePath };
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
     *  手机卡的IMSI    如：IMSI=460031304158115
     * @param context
     * @return
     */
    public static String getIMSI(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getSubscriberId();
    }

    /**
     * 获取手机的IMEI    如：IMEI=866946025546414
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getDeviceId();
    }

    /**
     * 检测当的网络（WLAN、3G/2G）状态
     * @param context Context
     * @return true 表示网络可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected())
            {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED &&
                        info.isAvailable())
                {
                    if(ping()){
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
     * @return
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
     * @return
     */
    public static String getEth0Mac(){
    	String macSerial = "";
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec(
                            "cat /sys/class/net/eth0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str;) {
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

            for (; null != str;) {
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
     * @param pContext
     * @param pBoolean  ture 开启移动数据，false 关闭移动数据
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })  
	public void setMobileData(Context pContext, boolean pBoolean)   
    {    
        try   
        {    
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
     * @param pContext  
     * @param arg 默认填null  
     * @return true 连接 false 未连接  
     */    
    @SuppressWarnings({ "rawtypes", "unchecked" })  
    public  boolean getMobileDataState(Context pContext, Object[] arg)   
    {    
        try   
        {    
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
     *	新增一个APN接入点 
     * APN：umim2m.njm2mapn或者umim2m.gzm2mapn
     * MCC：460 
     * MNC：06 
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
		Log.e("","add new APN id=" + id + "; APN--values="+values.toString());
		return id;
	}

	/**
	 * 罗列出所有的APN（默认的有很多APN,使用时最好根据apn进行过滤）
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
	 * @return
	 */
	public static boolean ExistSDCard() {  
	  if (android.os.Environment.getExternalStorageState().equals(  
	    android.os.Environment.MEDIA_MOUNTED)) {  
		  return true;  
	  } else{
		  return false;  
	  }
	}

    public static String getImgPath(){
    	String time = getYMDHMSFromLong(System.currentTimeMillis());
    	String mac = getEth0Mac();
    	return "/sdcard/JZP_SDCardLog/screenshot/"+time+"_"+mac+".png";
    }
    
    // Long时间格式化
    public static String getYMDHMSFromLong(Long date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        java.util.Date dt = new java.util.Date(date);
        String sDateTime = df.format(dt);
        return sDateTime;
    }
    
    public static String getYMDFromLong(Long date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date dt = new java.util.Date(date);
        String sDateTime = df.format(dt);
        return sDateTime;
    }

}
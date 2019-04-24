package com.kgd.tools.library;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 *   时间相关工具类
 *
 *   boolean getTwoHour(String st1, String st2)		二个时间间的差值,大于十分钟返回true否则返回false；
 *	long getCurrentTimeByHHmm(String hhmm)	由 hhmm 获取 当前的 long 时间
 *   addAndReduceMinute(String str, int value)	增加或减少几分钟 （如：str=09:50  value=-10 就是 09:40   value=10就是10:00）
 *   isValidHHmm(String str)	检查  str 是否HH:mm格式
 *   isLessThenCurrenTime   是否小于当前时间
 *  getStringDateFromLong	Long时间格式化为指定String 格式 ，  （如 1547693420668/HH:mm 转换为10:50）
 *	getYesterdayStartTime	返回昨天的开始时间 long （如 2019-04-24 00:00:00）
 *	getYesterdayEndTime		返回昨天的结束时间 long （如 2019-04-24 23:59:59）
 */
public class DateUtils {
    private static String mYear;
    private static String mMonth;
    private static String mDay;
    private static String mWay;

	// 2017年4月25日	星期二
	public static String getDateAndWeek(){
		final Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

		mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
		mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
		mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
		mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
		if("1".equals(mWay)){
			mWay ="天";
		}else if("2".equals(mWay)){
			mWay ="一";
		}else if("3".equals(mWay)){
			mWay ="二";
		}else if("4".equals(mWay)){
			mWay ="三";
		}else if("5".equals(mWay)){
			mWay ="四";
		}else if("6".equals(mWay)){
			mWay ="五";
		}else if("7".equals(mWay)){
			mWay ="六";
		}
		Log.e("--DateUtils--", mYear + "年" + mMonth + "月" + mDay+"日"+"\t星期"+mWay);
		return mYear + "年" + mMonth + "月" + mDay+"日"+"\t星期"+mWay;
	}
	
	public static String getDate(){
		final Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT"));
		mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
		mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
		mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
		
		return mYear + "-" + mMonth + "-" + mDay+"";
	}
	/**
	 * 修改系统时间
	 * @param datetime
	 */
	public void setSysDateTime(String datetime){
		try {
			Process process = Runtime.getRuntime().exec("su");
			//			String datetime="20131023.112800"; //测试的设置的时间【时间格式 yyyyMMdd.HHmmss】
			DataOutputStream os = new DataOutputStream(process.getOutputStream());
			os.writeBytes("setprop persist.sys.timezone GMT\n");
			os.writeBytes("/system/bin/date -s "+datetime+"\n");
			os.writeBytes("clock -w\n");
			os.writeBytes("exit\n");
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static final String FORMAT_DATE_YYMMDD_HHMMSS = "yyyy-MM-dd HH:mm:ss";
	/**
	 *
	 * long2String:把指定的时间转换为对应的格式化字符串. <br/>
	 *
	 * @author maple
	 * @param times
	 * @param template
	 * @return
	 * @since JDK 1.6
	 */
	public static String long2String(long times, String template) {
		if (times < 1 || template == null || ("").equals(template)) {
			return null;
		}
		Date date = new Date(times);
		SimpleDateFormat sdf = new SimpleDateFormat(template, Locale.CHINA);
		return sdf.format(date);
	}


	/**
	 *  二个时间间的差值,大于十分钟返回true否则返回false；
	 *   如： st1 = 11:10  st2=11:15   返回false
	 * @param st1
	 * @param st2
	 * @return
	 */
	public static boolean getTwoHour(String st1, String st2) {
		try {
			SimpleDateFormat df = new SimpleDateFormat("HH:mm");
			Date powerOffTime = df.parse(st1);
			Date powerOnTime = df.parse(st2);
			long diff = (powerOnTime.getTime() - powerOffTime.getTime()) / 60000;

			if (diff >= 10 && diff <= 1430) {
				return true;
			} else if (diff < 0 && diff >= -1430) {
				return true;
			} else {
				return false;
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	// 由 hhmm 获取 当前的 long 时间
	public static long getCurrentTimeByHHmm(String hhmm){
        try {
            if(!isValidHHmm(hhmm)) return 0;
            String split[] = hhmm.split(":");
            if(split.length != 2) return 0;
            String hh=split[0], mm=split[1];
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.set(Calendar.HOUR, Integer.valueOf(hh));
            cal.set(Calendar.MINUTE, Integer.valueOf(mm));
            Log.e("getCurrentTimeByHHmm", "getCurrentTimeByHHmm="+cal.getTimeInMillis());
            return cal.getTimeInMillis();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
    }
	/**
	 *  增加或减少几分钟
	 * @param str   如：09:50
	 * @param value  如 -10 就是 09:40   10就是10:00
	 * @return    如 00:03
	 */
	public static String addAndReduceMinute(String str, int value){
		try {
			if(!isValidHHmm(str)) return "00:00";
			SimpleDateFormat df = new SimpleDateFormat("HH:mm");
			Date date = df.parse(str);

			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.MINUTE, value);// 24小时制   x为需要增加或减少的时间

			int hour = cal.get(Calendar.HOUR_OF_DAY);	//24小时制
			int min = cal.get(Calendar.MINUTE);
			String strHour = hour<10 ? "0"+hour : ""+hour;
			String strMin = min<10 ? "0"+min : ""+min;
			return strHour+":"+strMin;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "00:00";
		}
	}

	/**
	 *  检查  str 是否HH:mm格式
	 * @param str
	 * @return
	 */
	public static boolean isValidHHmm(String str) {
		boolean convertSuccess=true;
		// 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		try {
			// 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
			format.setLenient(false);
			format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
			convertSuccess=false;
		}
		return convertSuccess;
	}

	/**
	 *  是否小于当前时间
	 * @return
	 */
	public static boolean isLessThenCurrenTime(String time, boolean defaultResult){
		boolean result = defaultResult;
		try{
			Calendar date= Calendar.getInstance();//获取当前时间
			Calendar startTime = (Calendar) date.clone();
			String[]startTimes = time.split(":");
			if(startTimes.length != 2) return false;
			startTime.set(Calendar.HOUR_OF_DAY, Integer.valueOf(startTimes[0]));//将一个时间设为当前8:00
			startTime.set(Calendar.MINUTE, Integer.valueOf(startTimes[1]));
			startTime.set(Calendar.SECOND, 0);
			result = date.after(startTime);
		}catch (Exception e){
			e.printStackTrace();
		}
		return result;
	}

	/**
	 *	Long时间格式化为指定String
	 * @param date			如1547693420668
	 * @param format		如HH:mm
	 * @return				如转为 10:50
	 */
	public static String getStringDateFromLong(Long date, String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		java.util.Date dt = new java.util.Date(date);
		String sDateTime = df.format(dt);
		return sDateTime;
	}

	/**
	 *
	 * @return  返回昨天的开始时间 long （如 2019-04-24 00:00:00）
	 */
	public static long getYesterdayStartTime(){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)-1);
		calendar.set(Calendar.HOUR,0);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND,0);
		calendar.set(Calendar.MILLISECOND,0);
		System.out.println("开始时间："+calendar.getTime());
		return calendar.getTimeInMillis();
	}

	/**
	 *
	 * @return  返回昨天的结束时间 long （如 2019-04-24 23:59:59）
	 */
	public static long getYesterdayEndTime(){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)-1);
		calendar.set(Calendar.HOUR,23);
		calendar.set(Calendar.MINUTE,59);
		calendar.set(Calendar.SECOND,59);
		calendar.set(Calendar.MILLISECOND,999);
		System.out.println("结束时间："+calendar.getTime());
		return calendar.getTimeInMillis();
	}

	public static void main(String[] args) {
		System.out.println(addAndReduceMinute("12:30", -10));
		System.out.println(System.currentTimeMillis());
		System.out.println(getStringDateFromLong(System.currentTimeMillis(), "HH:mm"));


	}
}



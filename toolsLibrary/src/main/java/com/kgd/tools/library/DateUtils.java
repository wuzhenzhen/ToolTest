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
 *   ʱ����ع�����
 *
 *   boolean getTwoHour(String st1, String st2)		����ʱ���Ĳ�ֵ,����ʮ���ӷ���true���򷵻�false��
 *	long getCurrentTimeByHHmm(String hhmm)	�� hhmm ��ȡ ��ǰ�� long ʱ��
 *   addAndReduceMinute(String str, int value)	���ӻ���ټ����� ���磺str=09:50  value=-10 ���� 09:40   value=10����10:00��
 *   isValidHHmm(String str)	���  str �Ƿ�HH:mm��ʽ
 *   isLessThenCurrenTime   �Ƿ�С�ڵ�ǰʱ��
 *  getStringDateFromLong	Longʱ���ʽ��Ϊָ��String ��ʽ ��  ���� 1547693420668/HH:mm ת��Ϊ10:50��
 *	getYesterdayStartTime	��������Ŀ�ʼʱ�� long ���� 2019-04-24 00:00:00��
 *	getYesterdayEndTime		��������Ľ���ʱ�� long ���� 2019-04-24 23:59:59��
 */
public class DateUtils {
    private static String mYear;
    private static String mMonth;
    private static String mDay;
    private static String mWay;

	// 2017��4��25��	���ڶ�
	public static String getDateAndWeek(){
		final Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

		mYear = String.valueOf(c.get(Calendar.YEAR)); // ��ȡ��ǰ���
		mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// ��ȡ��ǰ�·�
		mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// ��ȡ��ǰ�·ݵ����ں���
		mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
		if("1".equals(mWay)){
			mWay ="��";
		}else if("2".equals(mWay)){
			mWay ="һ";
		}else if("3".equals(mWay)){
			mWay ="��";
		}else if("4".equals(mWay)){
			mWay ="��";
		}else if("5".equals(mWay)){
			mWay ="��";
		}else if("6".equals(mWay)){
			mWay ="��";
		}else if("7".equals(mWay)){
			mWay ="��";
		}
		Log.e("--DateUtils--", mYear + "��" + mMonth + "��" + mDay+"��"+"\t����"+mWay);
		return mYear + "��" + mMonth + "��" + mDay+"��"+"\t����"+mWay;
	}
	
	public static String getDate(){
		final Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT"));
		mYear = String.valueOf(c.get(Calendar.YEAR)); // ��ȡ��ǰ���
		mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// ��ȡ��ǰ�·�
		mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// ��ȡ��ǰ�·ݵ����ں���
		
		return mYear + "-" + mMonth + "-" + mDay+"";
	}
	/**
	 * �޸�ϵͳʱ��
	 * @param datetime
	 */
	public void setSysDateTime(String datetime){
		try {
			Process process = Runtime.getRuntime().exec("su");
			//			String datetime="20131023.112800"; //���Ե����õ�ʱ�䡾ʱ���ʽ yyyyMMdd.HHmmss��
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
	 * long2String:��ָ����ʱ��ת��Ϊ��Ӧ�ĸ�ʽ���ַ���. <br/>
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
	 *  ����ʱ���Ĳ�ֵ,����ʮ���ӷ���true���򷵻�false��
	 *   �磺 st1 = 11:10  st2=11:15   ����false
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

	// �� hhmm ��ȡ ��ǰ�� long ʱ��
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
	 *  ���ӻ���ټ�����
	 * @param str   �磺09:50
	 * @param value  �� -10 ���� 09:40   10����10:00
	 * @return    �� 00:03
	 */
	public static String addAndReduceMinute(String str, int value){
		try {
			if(!isValidHHmm(str)) return "00:00";
			SimpleDateFormat df = new SimpleDateFormat("HH:mm");
			Date date = df.parse(str);

			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.MINUTE, value);// 24Сʱ��   xΪ��Ҫ���ӻ���ٵ�ʱ��

			int hour = cal.get(Calendar.HOUR_OF_DAY);	//24Сʱ��
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
	 *  ���  str �Ƿ�HH:mm��ʽ
	 * @param str
	 * @return
	 */
	public static boolean isValidHHmm(String str) {
		boolean convertSuccess=true;
		// ָ�����ڸ�ʽΪ��λ��/��λ�·�/��λ���ڣ�ע��yyyy/MM/dd���ִ�Сд��
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		try {
			// ����lenientΪfalse. ����SimpleDateFormat��ȽϿ��ɵ���֤���ڣ�����2007/02/29�ᱻ���ܣ���ת����2007/03/01
			format.setLenient(false);
			format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
// ���throw java.text.ParseException����NullPointerException����˵����ʽ����
			convertSuccess=false;
		}
		return convertSuccess;
	}

	/**
	 *  �Ƿ�С�ڵ�ǰʱ��
	 * @return
	 */
	public static boolean isLessThenCurrenTime(String time, boolean defaultResult){
		boolean result = defaultResult;
		try{
			Calendar date= Calendar.getInstance();//��ȡ��ǰʱ��
			Calendar startTime = (Calendar) date.clone();
			String[]startTimes = time.split(":");
			if(startTimes.length != 2) return false;
			startTime.set(Calendar.HOUR_OF_DAY, Integer.valueOf(startTimes[0]));//��һ��ʱ����Ϊ��ǰ8:00
			startTime.set(Calendar.MINUTE, Integer.valueOf(startTimes[1]));
			startTime.set(Calendar.SECOND, 0);
			result = date.after(startTime);
		}catch (Exception e){
			e.printStackTrace();
		}
		return result;
	}

	/**
	 *	Longʱ���ʽ��Ϊָ��String
	 * @param date			��1547693420668
	 * @param format		��HH:mm
	 * @return				��תΪ 10:50
	 */
	public static String getStringDateFromLong(Long date, String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		java.util.Date dt = new java.util.Date(date);
		String sDateTime = df.format(dt);
		return sDateTime;
	}

	/**
	 *
	 * @return  ��������Ŀ�ʼʱ�� long ���� 2019-04-24 00:00:00��
	 */
	public static long getYesterdayStartTime(){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)-1);
		calendar.set(Calendar.HOUR,0);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND,0);
		calendar.set(Calendar.MILLISECOND,0);
		System.out.println("��ʼʱ�䣺"+calendar.getTime());
		return calendar.getTimeInMillis();
	}

	/**
	 *
	 * @return  ��������Ľ���ʱ�� long ���� 2019-04-24 23:59:59��
	 */
	public static long getYesterdayEndTime(){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)-1);
		calendar.set(Calendar.HOUR,23);
		calendar.set(Calendar.MINUTE,59);
		calendar.set(Calendar.SECOND,59);
		calendar.set(Calendar.MILLISECOND,999);
		System.out.println("����ʱ�䣺"+calendar.getTime());
		return calendar.getTimeInMillis();
	}

	public static void main(String[] args) {
		System.out.println(addAndReduceMinute("12:30", -10));
		System.out.println(System.currentTimeMillis());
		System.out.println(getStringDateFromLong(System.currentTimeMillis(), "HH:mm"));


	}
}



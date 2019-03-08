package com.kgd.tools.library;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

//import com.lazy.library.logging.Logcat;
//import com.tiamaes.sectionstationscreen4gj.application.SSSApplication;

/**
 *  ֻ��������������log
 *  
 * @author wuzhenzhen@tiamaes.com
 *
 */
public class LocalLogUtil {
	private final String TAG = "LocalLogUtil";
	public static final String extAddr = "/mnt/extsd/JZPLog"; //log Ĭ�ϱ��浽 ����SDCard
	public static final String usbAddr = "mnt/usbhost1/JZPLog";
	private final String sdcardLogAddr = "/mnt/sdcard/JZP_SDCardLog"; //���û�� ����SDCard, �򱣴浽�ڲ�sdcard
	private static LocalLogUtil Instance;
	private static String copyPath = "/mnt/extsd/JZPLog";
	private LocalLogUtil(){}
	public static LocalLogUtil getInstance(){
		if(Instance == null){
			Instance = new LocalLogUtil();
		}
		return Instance;
	}
	
	private Context mContext;
	private final int COPY_LOG_FINISH = 100;
	public void HandleLog(Context context, String addr){
		this.mContext = context;
		this.copyPath = addr;
		new Thread(handleLog).start();
	}
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
				case COPY_LOG_FINISH:
					if(mContext != null){
						Toast.makeText(mContext, "��־�������", Toast.LENGTH_LONG).show();
					}
				break;
			}
		}
		
	};
	
	private Runnable handleLog = new Runnable() {
		
		@Override
		public void run() {
//			Logcat.iii("--LocalLogUtil:HandleLog--", TAG);
			//TODO  /mnt/sdcard/JZP_SDCardLog ֻ�����������
			doReserve3DayLog(sdcardLogAddr);
			doReserve3DayLog(sdcardLogAddr+"/crash");
			doReserve3DayLog(sdcardLogAddr+"/screenshot");
			
			//�����ڲ��洢�����log ������
			FileUtil.copyFolder(sdcardLogAddr, copyPath); 
			
			//TODO  /mnt/extsd/JZPLog  ֻ�����������log
			doReserve3DayLog(copyPath);
			doReserve3DayLog(copyPath+"/crash");
			doReserve3DayLog(copyPath+"/screenshot");
			
			//ɾ��δ�ϴ��ɹ���Log
			doDeleteZipLog();
			//TODO 
//			handler.sendEmptyMessage(COPY_LOG_FINISH);
		}
	};
	
    //TODO ��ʱֻ������������log�� ����SD��ʶ������ԭ�򣬲���log���� �������ڲ�����Ҫ���ڽ�log���浽����SDcard��
    private void doReserve3DayLog(String addr){
//    	Logcat.iii("--doReserve3DayLog--"+addr);
    	File Sfile = new File(addr);
    	if(!Sfile.exists()) return;
    	File[] fs = Sfile.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				if(pathname.getName().endsWith(".log")
						|| pathname.getName().endsWith(".png")){
					return true;
				}
				return false;
			}
		});
    	if(fs.length <=3) return;
		Arrays.sort(fs, new Comparator<File>() { //����  �޸�ʱ������  ���µ���
			public int compare(File f1, File f2) {
				long diff = f1.lastModified() - f2.lastModified();
				if (diff > 0)
					return 1;
				else if (diff == 0)
					return 0;
				else
					return -1;
			}

			public boolean equals(Object obj) {
				return true;
			}

		});
		// ֻ���������log
		if(fs.length>3){
			for (int i = fs.length - 1-3; i > -1; i--) {
//				Logcat.iii("--delete--"+fs[i].getName()+"; lastModified="+new Date(fs[i].lastModified()), TAG);
				fs[i].delete();
			}	
		}
    }
    
    //ɾ���ϴ�ʧ�ܵ�log zip(��ʡ�ռ�)
    private void doDeleteZipLog(){
//    	Logcat.iii("--doDeleteZipLog--");
    	File Sfile = new File("/sdcard");
    	if(!Sfile.exists()) return;
    	File[] fs = Sfile.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				if(pathname.getName().endsWith(AppUtils.getEth0Mac()+".zip")){
					return true;
				}
				return false;
			}
		});
    	if(fs.length <=1) return;
		Arrays.sort(fs, new Comparator<File>() { //����  �޸�ʱ������  ���µ���
			public int compare(File f1, File f2) {
				long diff = f1.lastModified() - f2.lastModified();
				if (diff > 0)
					return 1;
				else if (diff == 0)
					return 0;
				else
					return -1;
			}

			public boolean equals(Object obj) {
				return true;
			}

		});
		// ֻ�������һ�ε�zip
		if(fs.length>1){
			for (int i = fs.length - 1-1; i > -1; i--) {
//				Logcat.iii("--delete--"+fs[i].getName()+"; lastModified="+new Date(fs[i].lastModified()), TAG);
				fs[i].delete();
			}	
		}
    }
}

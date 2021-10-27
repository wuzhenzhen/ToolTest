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


/**
 *  只保存留最近三天的log
 *  
 * @author wzz
 *
 */
public class LocalLogUtil {
	private final String TAG = "LocalLogUtil";
	public static final String extAddr = "/mnt/extsd/JZPLog"; //log 默认保存到 外置SDCard
	public static final String usbAddr = "mnt/usbhost1/JZPLog";
	private final String sdcardLogAddr = "/mnt/sdcard/JZP_SDCardLog"; //如果没有 外置SDCard, 则保存到内部sdcard
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
						Toast.makeText(mContext, "日志处理完毕", Toast.LENGTH_LONG).show();
					}
				break;
			}
		}
		
	};
	
	private Runnable handleLog = new Runnable() {
		
		@Override
		public void run() {
//			Logcat.iii("--LocalLogUtil:HandleLog--", TAG);
			//TODO  /mnt/sdcard/JZP_SDCardLog 只保留最近三天
			doReserve3DayLog(sdcardLogAddr);
			doReserve3DayLog(sdcardLogAddr+"/crash");
			doReserve3DayLog(sdcardLogAddr+"/screenshot");
			
			//复制内部存储器里的log 到本地
			FileUtil.copyFolder(sdcardLogAddr, copyPath); 
			
			//TODO  /mnt/extsd/JZPLog  只保留最近三天log
			doReserve3DayLog(copyPath);
			doReserve3DayLog(copyPath+"/crash");
			doReserve3DayLog(copyPath+"/screenshot");
			
			//删除未上传成功的Log
			doDeleteZipLog();
			//TODO 
//			handler.sendEmptyMessage(COPY_LOG_FINISH);
		}
	};
	
    //TODO 暂时只保存最近三天的log， 由于SD卡识别慢的原因，部分log可能 可在在内部，需要定期将log保存到外置SDcard中
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
		Arrays.sort(fs, new Comparator<File>() { //根据  修改时间排序  由新到旧
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
		// 只保留三天的log
		if(fs.length>3){
			for (int i = fs.length - 1-3; i > -1; i--) {
//				Logcat.iii("--delete--"+fs[i].getName()+"; lastModified="+new Date(fs[i].lastModified()), TAG);
				fs[i].delete();
			}	
		}
    }
    
    //删除上传失败的log zip(节省空间)
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
		Arrays.sort(fs, new Comparator<File>() { //根据  修改时间排序  由新到旧
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
		// 只保留最后一次的zip
		if(fs.length>1){
			for (int i = fs.length - 1-1; i > -1; i--) {
//				Logcat.iii("--delete--"+fs[i].getName()+"; lastModified="+new Date(fs[i].lastModified()), TAG);
				fs[i].delete();
			}	
		}
    }
}

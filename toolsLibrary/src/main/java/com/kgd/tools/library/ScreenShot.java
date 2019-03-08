package com.kgd.tools.library;

import java.io.File;  
import java.io.FileNotFoundException;  
import java.io.FileOutputStream;  
import java.io.IOException;  

//import com.lazy.library.logging.Logcat;

import android.app.Activity;  
import android.graphics.Bitmap;  
import android.graphics.Rect;  
import android.view.View;  
  
public class ScreenShot {  
  
    private static Bitmap takeScreenShot(Activity activity) {  
        // View������Ҫ��ͼ��View  
        View view = activity.getWindow().getDecorView();  
        view.setDrawingCacheEnabled(true);  
        view.buildDrawingCache();  
        Bitmap b1 = view.getDrawingCache();  
  
        // ��ȡ״̬���߶�  
        Rect frame = new Rect();  
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);  
        int statusBarHeight = frame.top;  
  
        // ��ȡ��Ļ���͸�  
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();  
        int height = activity.getWindowManager().getDefaultDisplay()  
                .getHeight();  
        // ȥ��������  
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height  
                - statusBarHeight);  
        view.destroyDrawingCache();  
        return b;  
    }  
  
    private static void savePic(Bitmap b, File filePath) {  
        FileOutputStream fos = null;  
        try {  
            fos = new FileOutputStream(filePath);  
            if (null != fos) {  
                b.compress(Bitmap.CompressFormat.PNG, 100, fos);  
                fos.flush();  
                fos.close();  
                
		         // ���ж��Ƿ��Ѿ�����
		         if(b != null && !b.isRecycled()){ 
		             // ���ղ�����Ϊnull
		             b.recycle(); 
		             b = null; 
		         } 
		         System.gc();
            }  
        } catch (FileNotFoundException e) {  
            // e.printStackTrace();  
        } catch (IOException e) {  
            // e.printStackTrace();  
        }  
    }  
  
    public static void shoot(Activity a, File filePath) {  
        if (filePath == null) {  
            return;  
        }  
        if (!filePath.getParentFile().exists()) {  
            filePath.getParentFile().mkdirs();  
        }  
        ScreenShot.savePic(ScreenShot.takeScreenShot(a), filePath);  
    }  
    
    public static void shoot(Activity a){
    	try {
    		String path = AppUtils.getImgPath();
        	File imgFile = new File(path);
//        	Logcat.iii("--ScreenShot--shoot--imgFile--"+imgFile.getAbsolutePath());
        	shoot(a,imgFile);	
    	}catch(Exception e){
    		e.printStackTrace();
//    		Logcat.iii("--ScreenShot--shoot--"+e.getMessage());
    	}
    	
    }
    
    public static void takeScreenShot()
    {
    	String mSavedPath = AppUtils.getImgPath();
//    	Logcat.iii("--takeScreenShot--screencap--"+mSavedPath);
        ShellUtils.CommandResult result = ShellUtils.execCommand("screencap -p " + mSavedPath, true);
//        Logcat.iii("--takeScreenShot--screencap=="+result.toString());
    }
}  
package com.kgd.tools.library;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

@SuppressLint("NewApi")
public class ScreenShot {

	private static Bitmap takeScreenShot(Activity activity) {
		// View是你需要截图的View
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b1 = view.getDrawingCache();

		// 获取状态栏高度
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;

		// 获取屏幕长和高
		int width = activity.getWindowManager().getDefaultDisplay().getWidth();
		int height = activity.getWindowManager().getDefaultDisplay()
				.getHeight();
		// 去掉标题栏
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

				// 先判断是否已经回收
				if (b != null && !b.isRecycled()) {
					// 回收并且置为null
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

	public static void shoot(Activity a) {
		try {
			String path = AppUtils.getImgName();
			File imgFile = new File(path);
//			ZLog.iii("--ScreenShot--shoot--imgFile--"
//					+ imgFile.getAbsolutePath());
			shoot(a, imgFile);
		} catch (Exception e) {
			e.printStackTrace();
//			ZLog.iii("--ScreenShot--shoot--" + e.getMessage());
		}

	}

	public static void takeScreenShot() {
		String mSavedPath = AppUtils.getImgName();
		File imgFile = new File("/mnt/sdcard/EBSB_SDCardLog/screenshot");
		if (!imgFile.exists()) {
			imgFile.mkdirs();
		}
//		ZLog.iii("--takeScreenShot--screencap--" + mSavedPath);
		ShellUtils.CommandResult result = ShellUtils.execCommand("screencap -p "
				+ mSavedPath, true);
//		ZLog.iii("--takeScreenShot--screencap==" + result.toString());
	}

	/**
	 * * 截屏方法 Android4.0 -- Android4.2 android.view.Surface.screenshot()
	 * 隐藏方法，需要通过反射调用 Android4.3 之后 screenshot
	 * 移到android.view.SurfaceControl，SurfaceControl是隐藏类，需要通过反射调用
	 * 注：此方法必须要系统签名才可以!
	 * 
	 * @param context
	 * @param imagePath
	 *            保存路径 如：sdcard/123.png
	 * @return true 截屏成功，false截屏失败
	 */
	public static boolean takeScreenShot3(Context context, String imagePath){
        if(TextUtils.isEmpty(imagePath)){
            return false;
        }

        WindowManager mWindowManager;
        DisplayMetrics mDisplayMetrics;
        Display mDisplay;

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();
        mDisplayMetrics = new DisplayMetrics();
        mDisplay.getRealMetrics(mDisplayMetrics);

        float[] dims = {mDisplayMetrics.widthPixels , mDisplayMetrics.heightPixels };
        Class testClass;
        try {
            File imageFile = new File(imagePath);
            if (!imageFile.getParentFile().exists()) {
                boolean result = imageFile.getParentFile().mkdirs();
//                ZLog.iii("takeScreenShot3--result--"+result);
            }

            testClass = Class.forName("android.view.SurfaceControl");
            Method saddMethod1 = testClass.getMethod("screenshot", new Class[]{int.class ,int.class});
            Bitmap screenBitmap = (Bitmap) saddMethod1.invoke(null, new Object[]{(int) dims[0], (int) dims[1]});
            if (screenBitmap == null) {
//                ZLog.eee("----takeScreenShot3---screenBitmap is null--"+dims[0]+"--"+dims[1]);
                return false ;
            }
            FileOutputStream out = new FileOutputStream(imagePath);
            screenBitmap.compress(Bitmap.CompressFormat. PNG, 100, out);
        } catch (Exception e){
//            ZLog.eee("----takeScreenShot3---e="+e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true ;
    }
}  
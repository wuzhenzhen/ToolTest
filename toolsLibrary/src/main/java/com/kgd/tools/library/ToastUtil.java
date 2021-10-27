package com.kgd.tools.library;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by wzz on 2017/05/06.
 * wzz
 */

public class ToastUtil {
    private static Toast mToast;
    public static void showToast(Context context, int resId, int duration){
        showToast(context, context.getString(resId), duration);
    }
    public static void showToast(Context context, String msg, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, duration);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }
}

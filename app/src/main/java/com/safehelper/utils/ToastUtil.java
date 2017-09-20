package com.safehelper.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/9/20.
 */

public class ToastUtil {

    public static void doToast(Context context, String text){
        Toast toast = new Toast(context);
        toast.setText(text);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
        //Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}

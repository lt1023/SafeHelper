package com.safehelper.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/9/20.
 */

public class ToastUtil {

    public static void doToast(Context context, String text){
        Toast toast = Toast.makeText(context,text,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM,0,200);
        toast.show();
    }

}

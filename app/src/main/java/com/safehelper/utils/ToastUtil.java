package com.safehelper.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/9/20.
 */

public class ToastUtil {
    static Context mContext;
    public static void doToast(Context context, String text){
        mContext = context;
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}

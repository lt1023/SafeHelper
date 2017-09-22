package com.safehelper.utils;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by Administrator on 2017/9/21.
 */

public class UpdateUtil {
    /**
     * 将流转换成字符串
     * @param is
     * @return
     */
    public static String stream2String(InputStream is) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int temp = -1;
        try {
            while ((temp = is.read(buffer))!= -1){
                bos.write(buffer,0,temp);
            }
            return bos.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                bos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    /**
     * 获取版本名并在界面设置版本名称
     */
    public static String getPackageVersionName(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo("com.safehelper",0);
            String versionName = pi.versionName;
            return versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 获取版本号
     */
    public static int getPackageVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo("com.safehelper",0);
            int versionCode = pi.versionCode;
            return versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    /**
     * 第三方下载 ；xutils
     * compile 'org.xutils:xutils:3.3.36'
     * 下载最新版本apk文件
     */
    public static void downloadUpdate(final Context context, String url){
        final ProgressDialog pb = new ProgressDialog(context);
        RequestParams params = new RequestParams(url);
        String path = Environment.getExternalStorageDirectory()+File.separator + "safeHelper/safe.apk";
        Log.i("---------", path);
        params.setSaveFilePath(path);
        params.setAutoRename(true);
        x.http().post(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onSuccess(File result) {
                //下载完成，调用安卓系统安装apk
                Log.i("----------", "下载完成，准备安装");
                installApk(context,result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                Log.i("----------", "下载出错");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                pb.dismiss();
            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {
                Log.i("-------------", "开始下载");
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                Log.i("-------------", "下载中");
                pb.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pb.setMessage("亲，努力下载中。。。");
                pb.show();
                pb.setMax((int) total);
                pb.setProgress((int) current);
            }
        });
    }

    private static void installApk(Context context, File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apkFile),"application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}

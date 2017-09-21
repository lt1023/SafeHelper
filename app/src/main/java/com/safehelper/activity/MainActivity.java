package com.safehelper.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.safehelper.R;
import com.safehelper.utils.ToastUtil;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.safehelper.utils.UpdateUtil.getPackageVersion;
import static com.safehelper.utils.UpdateUtil.stream2String;

/**
 * Created by Administrator on 2017/9/20.
 */
public class MainActivity extends Activity{

    public static final String TAG = "----update----";
    //更新json的url
    private static final String update_url = "http://192.168.2.131:8080/app/update.json";
    //最新版本
    private static final int NEWESTVERSION = 0x123;
    //需要更新版本
    private static final int UPDATEVERSION = 0x124;
    //获取包的版本
    private float PACKAGEVERSION;
    //最新版本
    String versionName;
    //新版本介绍
    private String versionDesc;
    //新版本下载地址
    private String downloadUrl;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case NEWESTVERSION:
                    Bundle data = msg.getData();
                    versionName = data.getString("versionName");
                    versionDesc = data.getString("versionDesc");
                    downloadUrl = data.getString("downloadUrl");
                    showUpdateDialog(versionName,versionDesc,downloadUrl);
                break;
                case UPDATEVERSION:
                    ToastUtil.doToast(MainActivity.this,"恭喜你是最新的版本");
                    break;
                default:
                    return;
            }
        }
    };

    private void showUpdateDialog(String name, String desc,final String url) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(name + "版本更新");
        builder.setMessage(desc);
        builder.setPositiveButton("下次再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ToastUtil.doToast(MainActivity.this,"更新地址是：" + url);
            }
        });
        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        update();
    }

    private void update() {
        PACKAGEVERSION = Float.parseFloat(getPackageVersion(this)) ;
        getNewestVersion(PACKAGEVERSION);
    }
    /**
     * 检测版本更新-----在MainActivity中检测
     * @return
     */
    public void getNewestVersion(final float currentVersion){
        new Thread(){
            @Override
            public void run() {
                Message message ;
                Bundle data = new Bundle();
                try {
                    URL updateUrl = new URL(update_url);
                    HttpURLConnection connection = (HttpURLConnection) updateUrl.openConnection();
                    connection.setConnectTimeout(5*1000);
                    connection.setReadTimeout(3*1000);
                    InputStream is = connection.getInputStream();
                    String updateInfo = stream2String(is);
                    JSONObject updatejson = new JSONObject(updateInfo);
                    final String versionCode = updatejson.getString("versionCode");
                    final String versionName = updatejson.getString("versionName");
                    final String versionDesc = updatejson.getString("versionDesc");
                    final String downloadUrl = updatejson.getString("downloadUrl");
                    data.putString("versionCode",versionCode);
                    data.putString("versionName",versionName);
                    data.putString("versionDesc",versionDesc);
                    data.putString("downloadUrl",downloadUrl);
                    if (currentVersion <Float.parseFloat(versionCode)){
                        Log.i(TAG,"最新版本为:" + versionName);
                        message = new Message();
                        message.what = NEWESTVERSION;
                        message.setData(data);
                        mHandler.sendMessage(message);
                    }else {
                        message = new Message();
                        message.what = UPDATEVERSION;
                        mHandler.sendMessage(message);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
}

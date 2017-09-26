package com.safehelper.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.safehelper.R;
import com.safehelper.utils.ToastUtil;
import com.safehelper.utils.UpdateUtil;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import static com.safehelper.utils.UpdateUtil.getPackageVersionCode;
import static com.safehelper.utils.UpdateUtil.stream2String;

/**
 * Created by Administrator on 2017/9/20.
 */
public class MainActivity extends Activity{

    public static final String TAG = "----update----";
    //更新json的url
    //private static final String update_url = "http://www.zerown.top/app/update.json";
    private static final String update_url = "http://192.168.2.129:8080/app/update.json";

    //最新版本
    private static final int NEWESTVERSION = 0x123;
    //需要更新版本
    private static final int UPDATEVERSION = 0x124;
    //网络连接错误
    private static final int URLERROR = 0x125;
    //读取错误错误
    private static final int IOERROR = 0x126;
    //JSON格式错误错误
    private static final int JSONERROR = 0x127;

    //获取包的版本
    private int PACKAGEVERSION;
    //最新版本
    String versionName;
    //新版本介绍的
    private String versionDesc;
    //新版本下载地址
    private String downloadUrl;

    private TextView marqueeTV;
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
                case URLERROR:
                    ToastUtil.doToast(MainActivity.this,"网络请求失败");
                    break;
                case IOERROR:
                    ToastUtil.doToast(MainActivity.this,"读取错误");
                    break;
                case JSONERROR:
                    ToastUtil.doToast(MainActivity.this,"json格式错误");
                    break;
                default:
                    return;
            }
        }
    };

    private void showUpdateDialog(String name, String desc,final String url) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("版本更新");
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
                Log.i(TAG, "你点击了更新按钮");
                UpdateUtil.downloadUpdate(MainActivity.this,url);
            }
        });
        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        update();
        initView();
    }

    /**
     * 初始化View
     */
    private void initView() {
        marqueeTV = (TextView) findViewById(R.id.tv_marquee);
        marqueeTV.setText(R.string.main_marquee_text);
    }

    private void update() {
        PACKAGEVERSION = getPackageVersionCode(this);
        getNewestVersion(PACKAGEVERSION);
    }
    /**
     * 检测版本更新-----在MainActivity中检测
     * @return
     */
    public void getNewestVersion(final int currentVersion){
        new Thread(){
            @Override
            public void run() {
                Message message = new Message();;
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
                    if (currentVersion < Integer.parseInt(versionCode)){
                        Log.i(TAG,"最新版本为:" + versionName);
                        message.what = NEWESTVERSION;
                        message.setData(data);
                    }else {
                        message.what = UPDATEVERSION;
                    }
                } catch (MalformedURLException e) {

                    message.what = URLERROR;
                } catch (IOException e) {
                    e.printStackTrace();
                    message.what = IOERROR;
                }catch (JSONException e){
                    e.printStackTrace();
                    message.what = JSONERROR;
                }
                mHandler.sendMessage(message);
            }
        }.start();
    }
}

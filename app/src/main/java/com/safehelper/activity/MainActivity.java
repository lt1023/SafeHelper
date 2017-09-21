package com.safehelper.activity;

import android.app.Activity;
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
    private String PACKAGEVERSION;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case NEWESTVERSION:
                    String versionName = msg.obj.toString();
                    ToastUtil.doToast(MainActivity.this,"最新版本是：" + versionName + "请更新");
                break;
                case UPDATEVERSION:
                    ToastUtil.doToast(MainActivity.this,"恭喜你是最新的版本");
                    break;
                default:
                    return;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        update();
    }

    private void update() {
        PACKAGEVERSION = getPackageVersion(this);
        getNewestVersion(PACKAGEVERSION);
    }
    /**
     * 检测版本更新-----在MainActivity中检测
     * @return
     */
    public void getNewestVersion(final String currentVersion){
        new Thread(){
            @Override
            public void run() {
                Message message ;
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
                    if (currentVersion != versionCode){
                        Log.i(TAG,"最新版本为:" + versionName);
                        message = new Message();
                        message.what = NEWESTVERSION;
                        message.obj = versionName;
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

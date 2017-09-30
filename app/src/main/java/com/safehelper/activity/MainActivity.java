package com.safehelper.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.safehelper.R;
import com.safehelper.utils.ConstantSet;
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
    //自动切换viewpager
    private static final int AUTONEXT = 0x128;

    //获取包的版本
    private int PACKAGEVERSION;
    //最新版本
    String versionName;
    //新版本介绍的
    private String versionDesc;
    //新版本下载地址
    private String downloadUrl;

    private TextView marqueeTV;
    private GridView gv_home;
    private ViewPager mVpager_home;

    private String[] titles;
    private int[] images;
    private int[] imgs_bananers;
    private ImageView[] bananers;

    //退出间隔时间
    private long exitTime;
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
                case AUTONEXT:
                    int index = mVpager_home.getCurrentItem();
                    mVpager_home.setCurrentItem((index + 1) % imgs_bananers.length);
                    mHandler.sendEmptyMessageDelayed(AUTONEXT , 10*1000);
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
        initData();
        mHandler.sendEmptyMessageDelayed(AUTONEXT , 10*1000);
        initGirdViewListener();
    }

    //初始化功能点击监听器
    private void initGirdViewListener() {
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                switch (position){
                    case 0:
                        ToastUtil.doToast(getApplicationContext(),position + "被点击了");
                        break;
                    case 8:
                        intent.setClass(getApplicationContext(),SettinActivity.class);
                        startActivity(intent);
                }
            }
        });
    }

    //初始化数据
    private void initData() {
        marqueeTV.setText(ConstantSet.MARQUEE_TEXT);

        titles = new String[]{
                "手机防盗","通信卫士","软件管理","进程管理","流量统计","手机杀毒","缓存管理","高级工具","设置中心"
        };
        images = new int[]{
                R.drawable.image_phonethefe_selector, R.drawable.image_communicate_selector,
                R.drawable.image_appmanager_selector, R.drawable.image_progmanager_selector,
                R.drawable.image_netcount_selector, R.drawable.image_mcafee_selector,
                R.drawable.image_cachemanager_selector,R.drawable.image_supertool_selector,
                R.drawable.image_settingcenter_selector
        };
        imgs_bananers = new int[]{
                R.drawable.bananer01, R.drawable.bananer02, R.drawable.bananer03
        };
        bananers = new ImageView[imgs_bananers.length];
        for (int i = 0; i < imgs_bananers.length; i++) {
            ImageView imgview = new ImageView(this);
            bananers[i] = imgview;
            imgview.setBackgroundResource(imgs_bananers[i]);
        }
        gv_home.setAdapter(new GridViewAdapter());
        mVpager_home.setAdapter(new MPagerAdapter());
    }

    /**
     * 初始化View
     */
    private void initView() {
        marqueeTV = (TextView) findViewById(R.id.tv_marquee);
        gv_home = (GridView) findViewById(R.id.gv_home);
        gv_home.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mVpager_home = (ViewPager) findViewById(R.id.vpager_home);
    }

    /**
     * 检测更新
     */
    private void update() {
        PACKAGEVERSION = getPackageVersionCode(this);
        getNewestVersion(PACKAGEVERSION);
    }
    /**
     * 检测最新版本-----在MainActivity中检测
     * @return
     */
    public void getNewestVersion(final int currentVersion){
        new Thread(){
            @Override
            public void run() {
                Message message = new Message();
                Bundle data = new Bundle();
                try {
                    URL updateUrl = new URL(ConstantSet.UPDATE_URL);
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

    private class GridViewAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Object getItem(int position) {
            return titles[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.gv_item, null);
            ImageView iv_image_item = (ImageView) view.findViewById(R.id.iv_image_item);
            TextView tv_title_item = (TextView) view.findViewById(R.id.tv_title_item);
            iv_image_item.setBackgroundResource(images[position]);
            tv_title_item.setText(titles[position]);
            return view;
        }
    }

    /**
     * bananer图适配器
     */
    private class MPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return bananers.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(bananers[position]);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(bananers[position]);
            return bananers[position];
        }
    }
    //双击返回键退出应用
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtil.doToast(this,"再点一次退出");
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }
}

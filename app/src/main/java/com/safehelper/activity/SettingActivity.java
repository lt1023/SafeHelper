package com.safehelper.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import com.safehelper.R;
import com.safehelper.utils.ToastUtil;

/**
 * Created by Administrator on 2017/9/30.
 */
public class SettingActivity extends BaseActivity {
    private Switch switch_update;
    public static SharedPreferences mSettingSP;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        init();
    }
    //初始化数据
    private void init() {
        //初始化数据
        initData();
        //初始化标题
        initTitleText("setting");
        //初始化view
        initView();
        //初始化Sp-----shareprefence
        initSharePre();
        //初始化设置
        initSetting();
    }

    private void initSharePre() {
        mSettingSP = getSharedPreferences("setting",MODE_PRIVATE);
        mEditor = mSettingSP.edit();
    }

    private void initSetting() {
        switch_update.setChecked(mSettingSP.getBoolean("isAutoUpdate",true));
        switch_update.setOnCheckedChangeListener(new CheckChangeListener());
    }

    private void initView() {
        switch_update = (Switch) findViewById(R.id.switch_update);
    }

    private class CheckChangeListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                ToastUtil.doToast(getApplicationContext(),"开启更新提示！");
                mEditor.putBoolean("isAutoUpdate",true);
                mEditor.apply();
                return;
            }
            ToastUtil.doToast(getApplicationContext(),"关闭更新提示！");
            mEditor.putBoolean("isAutoUpdate",false);
            mEditor.apply();
        }
    }
}

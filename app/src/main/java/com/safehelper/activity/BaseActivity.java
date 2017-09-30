package com.safehelper.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.safehelper.R;

/**
 * Created by Administrator on 2017/9/30.
 */

public class BaseActivity extends Activity implements View.OnClickListener {
    private Button button_back;
    private TextView title;

    public void initData(){
        button_back = (Button) findViewById(R.id.activity_item_backbtn);
        button_back.setOnClickListener(this);
    }

    public void initTitleText(String text){
        title = (TextView) findViewById(R.id.activity_item_title);
        title.setText(text);
    }
    @Override
    public void onClick(View v) {
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }


}

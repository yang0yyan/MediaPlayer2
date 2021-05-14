package com.yy.mediaplayer.base;

import android.app.ActionBar;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yy.mediaplayer.utils.ActivityManagerUtil;


public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        SystemBarUtil.setStatusBarColor(this, R.color.TRANSPARENT);
        ActivityManagerUtil.getInstance().addActivity(this);
//        setScreenRoate(true);

        setContentView(getLayoutId());
        initView();
        createPresenter_();
        initData();

    }

    protected abstract View getLayoutId();

    protected abstract void initView();

    protected abstract void initData();

    protected void createPresenter_() {

    }

    private void hideActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.hide();
    }

    private void setScreenRoate(Boolean screenRoate) {
        if (screenRoate) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//设置竖屏模式
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManagerUtil.getInstance().removeActivity(this);
    }
}

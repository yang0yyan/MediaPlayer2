package com.yy.mediaplayer.activity;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.yy.mediaplayer.R;
import com.yy.mediaplayer.base.BaseActivity;
import com.yy.mediaplayer.databinding.ActivityLogonBinding;

public class LogonActivity extends BaseActivity implements View.OnClickListener{


    private ActivityLogonBinding binding;

    @Override
    protected View getLayoutId() {
        binding = ActivityLogonBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void initView() {
        binding.btnLogon.setOnClickListener(this);

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_logon:{
                binding.mlLogon.transitionToState(R.id.start_logon);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            setResult(0X02);
                            finish();
                            overridePendingTransition(0,0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
                break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(0X02);
            finish();
            overridePendingTransition(0,0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

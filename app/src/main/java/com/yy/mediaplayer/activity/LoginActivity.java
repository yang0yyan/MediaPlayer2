package com.yy.mediaplayer.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.motion.widget.MotionScene;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yy.mediaplayer.R;
import com.yy.mediaplayer.base.BaseActivity;
import com.yy.mediaplayer.databinding.ActivityLoginBinding;

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    ActivityLoginBinding binding ;

    @Override
    protected View getLayoutId() {
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void initView() {
        binding.btnLogin.setOnClickListener(this);
        binding.tvLogon.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:{
                binding.ml.transitionToState(R.id.start_login);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                            startActivityForResult(new Intent(LoginActivity.this,MainActivity.class),0X01);
                            finish();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

                break;
            }
            case R.id.tv_logon:{
                binding.ml.transitionToState(R.id.start_login);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                            startActivityForResult(new Intent(LoginActivity.this,LogonActivity.class),0X01);
                            overridePendingTransition(0,0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }

        }
    }

    private void animJump(Intent intent){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    startActivityForResult(intent,0X01);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0X01&&resultCode==0X02){
            binding.ml.transitionToState(R.id.end_login);
        }
    }
}
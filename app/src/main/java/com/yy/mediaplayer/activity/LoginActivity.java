package com.yy.mediaplayer.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;

import androidx.annotation.Nullable;

import com.yy.mediaplayer.R;
import com.yy.mediaplayer.base.BaseNetActivity;
import com.yy.mediaplayer.databinding.ActivityLoginBinding;
import com.yy.mediaplayer.net.presenter.LoginPresenter;
import com.yy.mediaplayer.net.view.LoginView;
import com.yy.mediaplayer.utils.ToastUtil;

public class LoginActivity extends BaseNetActivity<LoginPresenter> implements View.OnClickListener, LoginView.view {

    ActivityLoginBinding binding;

    @Override
    protected View getLayoutId() {
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void initView() {
        super.initView();
//        StatusBarUtil.setTranslucentStatus(this);
        binding.btnLogin.setOnClickListener(this);
        binding.tvLogon.setOnClickListener(this);
    }

    @Override
    protected LoginPresenter createRoomPresenter() {
        return new LoginPresenter(this);
    }


    @Override
    protected void initData() {
        SharedPreferences sp = getSharedPreferences("mp.sp", MODE_PRIVATE);
        binding.etUsername.setText(sp.getString("username", ""));
        binding.etPassword.setText(sp.getString("password", ""));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login: {
                login();
                break;
            }
            case R.id.tv_logon: {
                binding.ml.transitionToState(R.id.start_login);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                            overridePendingTransition(0, 0);
                            startActivityForResult(new Intent(LoginActivity.this, SignUpActivity.class), 0X01);
                            overridePendingTransition(0, 0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
                break;
            }

        }
    }

    private void login() {
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        if (username.equals("") || password.equals("")) {
            ToastUtil.showToast("用户名或密码不能为空");
            return;
        }
        if (!username.equals("111111") || !password.equals("111111")) {
            ToastUtil.showToast("用户名或密码错误");
            return;
        }
        mRoomPresenter.toLogin(username, password);
        SharedPreferences sp = getSharedPreferences("mp.sp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0X01 && resultCode == 0X02) {
            binding.ml.transitionToState(R.id.end_login);
        }
    }

    @Override
    public void onLoginSuccess(String token) {
        if ("123".equals(token)) {
            binding.ml.transitionToState(R.id.start_login);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }
}
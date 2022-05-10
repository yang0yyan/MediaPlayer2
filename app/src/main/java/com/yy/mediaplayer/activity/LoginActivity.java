package com.yy.mediaplayer.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.yy.mediaplayer.R;
import com.yy.mediaplayer.base.BaseNetActivity;
import com.yy.mediaplayer.databinding.ActivityLoginBinding;
import com.yy.mediaplayer.net.presenter.LoginPresenter;
import com.yy.mediaplayer.net.view.LoginView;
import com.yy.mediaplayer.utils.DensityUtil;
import com.yy.mediaplayer.utils.ToastUtil;

public class LoginActivity extends BaseNetActivity<LoginPresenter> implements View.OnClickListener, View.OnFocusChangeListener, LoginView.view {

    ActivityLoginBinding binding;
    private boolean agreementChecked = false;

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
        binding.loginAgreementTip.setOnClickListener(this);

        binding.etUsername.setOnFocusChangeListener(this);
        binding.etPassword.setOnFocusChangeListener(this);
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
        agreementChecked = sp.getBoolean("agreementChecked", false);
        setCheckedStatus(agreementChecked);
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
                            startActivityForResult(new Intent(LoginActivity.this, SignUpActivity.class), 0X01,null);
                            overridePendingTransition(0, 0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
                break;
            }
            case R.id.login_agreement_tip:
                agreementChecked = !agreementChecked;
                setCheckedStatus(agreementChecked);
                break;
        }
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.et_username:
                Drawable drawable;
                if (hasFocus) {
                    drawable = ResourcesCompat.getDrawable(getResources(), R.mipmap.login_username_focus_icon, null);
                } else {
                    drawable = ResourcesCompat.getDrawable(getResources(), R.mipmap.login_username_icon, null);
                }
                if (drawable != null) {
                    drawable.setBounds(0, 0, DensityUtil.dip2px(this, 13.5f), DensityUtil.dip2px(this, 16));
                }
                binding.etUsername.setCompoundDrawables(drawable, null, null, null);
                break;
            case R.id.et_password:
                Drawable drawable2;
                if (hasFocus) {
                    drawable2 = ResourcesCompat.getDrawable(getResources(), R.mipmap.login_password_focus_icon, null);
                } else {
                    drawable2 = ResourcesCompat.getDrawable(getResources(), R.mipmap.login_password_icon, null);
                }
                if (drawable2 != null) {
                    drawable2.setBounds(0, 0, DensityUtil.dip2px(this, 13), DensityUtil.dip2px(this, 16));
                }
                binding.etPassword.setCompoundDrawables(drawable2, null, null, null);
                break;
        }
    }

    public void setCheckedStatus(boolean status) {
        Drawable drawable;
        if (status) {
            drawable = ResourcesCompat.getDrawable(getResources(), R.mipmap.checked_icon, null);
        } else {
            drawable = ResourcesCompat.getDrawable(getResources(), R.mipmap.uncheck_icon, null);
        }
        if (drawable != null) {
            drawable.setBounds(0, 0, DensityUtil.dip2px(this, 14), DensityUtil.dip2px(this, 14));
        }
        binding.loginAgreementTip.setCompoundDrawables(drawable, null, null, null);
    }


    private void login() {
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        if (username.equals("") || password.equals("")) {
            ToastUtil.showToast("用户名或密码不能为空");
            return;
        }
        mRoomPresenter.toLogin(username, password);
        SharedPreferences sp = getSharedPreferences("mp.sp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putBoolean("agreementChecked", agreementChecked);
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
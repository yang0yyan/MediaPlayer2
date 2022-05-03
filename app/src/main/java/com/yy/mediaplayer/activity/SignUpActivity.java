package com.yy.mediaplayer.activity;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.yy.mediaplayer.R;
import com.yy.mediaplayer.base.BaseActivity;
import com.yy.mediaplayer.base.BaseNetMediaActivity;
import com.yy.mediaplayer.databinding.ActivitySignUpBinding;
import com.yy.mediaplayer.net.presenter.LocalMusicPresenter;
import com.yy.mediaplayer.net.presenter.SignUpPresenter;
import com.yy.mediaplayer.net.view.LocalMusicView;
import com.yy.mediaplayer.net.view.SingUpView;
import com.yy.mediaplayer.room.AppDatabase;
import com.yy.mediaplayer.room.DBManager;
import com.yy.mediaplayer.room.dao.UserInfoDao;
import com.yy.mediaplayer.room.entity.UserInfoEntity;
import com.yy.mediaplayer.utils.ToastUtil;

import io.reactivex.Single;

public class SignUpActivity extends BaseNetMediaActivity<SignUpPresenter> implements SingUpView.view, View.OnClickListener {

    private ActivitySignUpBinding binding;

    @Override
    protected View getLayoutId() {
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void initView() {
        binding.btnSignUp.setOnClickListener(this);

    }

    @Override
    protected SignUpPresenter createRoomPresenter() {
        return new SignUpPresenter(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_up: {
                String userName = binding.etUsername.getText().toString().trim();
                String password = binding.etPassword.getText().toString().trim();
                String password2 = binding.etPassword2.getText().toString().trim();
                String email = binding.etEmail.getText().toString().trim();
                String phone = binding.etPhone.getText().toString().trim();

                if (TextUtils.isEmpty(userName)) {
                    ToastUtil.showToast("请输入用户名");
                    break;
                }
                if (TextUtils.isEmpty(password)) {
                    ToastUtil.showToast("请输入密码");
                    break;
                }
                if (TextUtils.isEmpty(password2)) {
                    ToastUtil.showToast("请再次输入密码");
                    break;
                }
                if(!password.equals(password2)){
                    ToastUtil.showToast("两次密码不一致！");
                    break;
                }
                UserInfoEntity entity = new UserInfoEntity();
                entity.setUserName(userName);
                entity.setPassword(password);
                entity.setEmail(email);
                entity.setPhone(phone);
                mPresenter.insertUser(entity);
                break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(0X02);
            finish();
            overridePendingTransition(0, 0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onInsertSuccess() {
        finishPage();
    }

    private void finishPage() {
        binding.mlLogon.transitionToState(R.id.start_logon);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    setResult(0X02);
                    finish();
                    overridePendingTransition(0, 0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

}

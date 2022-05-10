package com.yy.mediaplayer.activity;

import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;

import com.yy.mediaplayer.R;
import com.yy.mediaplayer.base.BaseActivity;
import com.yy.mediaplayer.databinding.ActivityLogin2Binding;
import com.yy.mediaplayer.utils.DensityUtil;
import com.yy.mediaplayer.utils.ToastUtil;

public class Login2Activity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {
    ActivityLogin2Binding binding;
    private boolean agreementChecked = false;


    @Override
    protected View getLayoutId() {
        binding = ActivityLogin2Binding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void initView() {
        binding.loginBtn.setOnClickListener(this);
        binding.loginAgreementTip.setOnClickListener(this);

        binding.etUsername.setOnFocusChangeListener(this);
        binding.etPassword.setOnFocusChangeListener(this);
    }

    @Override
    protected void initData() {
        setCheckedStatus(agreementChecked);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                if (!agreementChecked) {
                    ToastUtil.showToast("请勾线许可协议");
                    break;
                }
                binding.ml.transitionToState(R.id.start_login);
                break;
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
}
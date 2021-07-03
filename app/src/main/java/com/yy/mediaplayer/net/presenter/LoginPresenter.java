package com.yy.mediaplayer.net.presenter;

import com.yy.mediaplayer.net.BasePresenter;
import com.yy.mediaplayer.net.view.LoginView;

public class LoginPresenter extends BasePresenter<LoginView.view> implements LoginView.presenter {
    public LoginPresenter(LoginView.view baseView) {
        super(baseView);
    }

    @Override
    public void toLogin(String username, String password) {
        baseView.onLoginSuccess("123");
    }
}

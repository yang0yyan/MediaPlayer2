package com.yy.mediaplayer.net.view;

import com.yy.mediaplayer.net.BaseView;

public interface LoginView {
    interface view extends BaseView{
        void onLoginSuccess(String token);
    }

    interface presenter {
        void toLogin(String username,String password);
    }
}

package com.yy.mediaplayer.net.presenter;

import com.yy.mediaplayer.net.BasePresenter;
import com.yy.mediaplayer.net.view.LoginView;
import com.yy.mediaplayer.room.RoomBaseConsumer;
import com.yy.mediaplayer.room.dao.UserInfoDao;
import com.yy.mediaplayer.room.entity.UserInfoEntity;

import java.util.List;

public class LoginPresenter extends BasePresenter<LoginView.view> implements LoginView.presenter {
    private final UserInfoDao dao;

    public LoginPresenter(LoginView.view baseView) {
        super(baseView);
        dao = db.userInfoDao();
    }

    @Override
    public void toLogin(String username, String password) {
        addDisposable(dao.getUser(username), new RoomBaseConsumer<List<UserInfoEntity>>() {
            @Override
            public void onSuccess(List<UserInfoEntity> o) {
                if(o.size()==1 && password.equals(o.get(0).getPassword())){
                    baseView.onLoginSuccess("123");
                }else{
                    baseView.showError("用户名或密码错误");
                }

            }

            @Override
            public void onError(String msg) {
                baseView.showError(msg);
            }
        });
    }
}

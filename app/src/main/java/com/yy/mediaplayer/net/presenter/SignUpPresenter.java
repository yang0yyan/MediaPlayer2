package com.yy.mediaplayer.net.presenter;

import com.yy.mediaplayer.net.BasePresenter;
import com.yy.mediaplayer.net.view.SingUpView;
import com.yy.mediaplayer.room.RoomBaseCompletable;
import com.yy.mediaplayer.room.dao.UserInfoDao;
import com.yy.mediaplayer.room.entity.UserInfoEntity;

public class SignUpPresenter extends BasePresenter<SingUpView.view> implements SingUpView.presenter {

    private final UserInfoDao dao;

    public SignUpPresenter(SingUpView.view baseView) {
        super(baseView);
        dao = db.userInfoDao();
    }

    @Override
    public void insertUser(UserInfoEntity infos) {
        addDisposable(dao.insert(infos), new RoomBaseCompletable(baseView) {
            @Override
            public void onSuccess() {
                baseView.onInsertSuccess();
            }

            @Override
            public void onError(String msg) {
                if (msg.contains("user_info.userName") && msg.contains("SQLITE_CONSTRAINT_PRIMARYKEY")) {
                    baseView.showError("用户名已存在");
                } else {
                    baseView.showError(msg);
                }
            }
        });
    }
}

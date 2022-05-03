package com.yy.mediaplayer.net.view;

import com.yy.mediaplayer.net.BaseView;
import com.yy.mediaplayer.room.entity.UserInfoEntity;

public interface SingUpView extends BaseView {
    interface view extends BaseView {
        void onInsertSuccess();
    }

    interface presenter {
        void insertUser(UserInfoEntity infos);
    }
}

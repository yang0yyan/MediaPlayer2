package com.yy.mediaplayer;

import android.app.Application;

import com.yy.mediaplayer.model.protocol.StreamProtocolFactory;
import com.yy.mediaplayer.room.DBManager;
import com.yy.mediaplayer.utils.ActivityManagerUtil;
import com.yy.mediaplayer.utils.ToastUtil;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        ToastUtil.init(this);
        DBManager.init(this);
        StreamProtocolFactory.setAppContext(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        DBManager.release();
        ActivityManagerUtil.getInstance().finishActivitys();
    }
}

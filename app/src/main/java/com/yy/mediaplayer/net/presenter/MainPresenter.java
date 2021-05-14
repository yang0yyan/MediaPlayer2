package com.yy.mediaplayer.net.presenter;

import android.util.Log;

import com.yy.mediaplayer.net.BaseModel;
import com.yy.mediaplayer.net.BaseObserver;
import com.yy.mediaplayer.net.BasePresenter;
import com.yy.mediaplayer.net.bean.MusicBean;
import com.yy.mediaplayer.net.view.MainView;
import com.yy.mediaplayer.utils.LogHelper;

import java.util.List;

import io.reactivex.annotations.NonNull;

public class MainPresenter extends BasePresenter<MainView.view> implements MainView.presenter{

    public MainPresenter(MainView.view baseView) {
        super(baseView);
    }

    @Override
    public void getMusicList() {
        addDisposable(apiServer.getMusic(), new BaseObserver<MusicBean>() {
            @Override
            public void onSuccess(BaseModel<MusicBean> o) {
                LogHelper.d("TAG",o.toString());
                baseView.onSuccess(o.getResult());
            }

            @Override
            public void onError(String msg) {
                baseView.showError(msg);
                Log.d("TAG", "onError: "+msg);
            }
        });
    }
}

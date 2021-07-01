package com.yy.mediaplayer.net.presenter;

import android.util.Log;

import com.yy.mediaplayer.net.bean.BaseModel;
import com.yy.mediaplayer.net.BaseObserver;
import com.yy.mediaplayer.net.BasePresenter;
import com.yy.mediaplayer.net.bean.MusicBean;
import com.yy.mediaplayer.net.view.CastMusicView;

public class CastMusicPresenter extends BasePresenter<CastMusicView.view> implements CastMusicView.presenter {
    public CastMusicPresenter(CastMusicView.view baseView) {
        super(baseView);
    }

    @Override
    public void getMusicList() {
        addDisposable(apiServer.getMusic(), new BaseObserver<MusicBean>() {
            @Override
            public void onSuccess(BaseModel<MusicBean> o) {
                baseView.onSuccess(o.getMusic());
            }

            @Override
            public void onError(String msg) {
                baseView.showError(msg);
            }

        });
    }
}

package com.yy.mediaplayer.net.presenter;

import com.yy.mediaplayer.net.BasePresenter;
import com.yy.mediaplayer.net.view.LocalMusicView;
import com.yy.mediaplayer.room.RoomBaseCompletable;
import com.yy.mediaplayer.room.RoomBaseConsumer;
import com.yy.mediaplayer.room.dao.MusicInfoDao;
import com.yy.mediaplayer.room.entity.MusicInfoEntity;

import java.util.List;

public class LocalMusicPresenter extends BasePresenter<LocalMusicView.view> implements LocalMusicView.presenter {
    MusicInfoDao dao;

    public LocalMusicPresenter(LocalMusicView.view baseView) {
        super(baseView);
        dao = db.musicInfoDao();
    }

    @Override
    public void getMusic() {
        addDisposable(dao.getAll(), new RoomBaseConsumer<List<MusicInfoEntity>>() {
            @Override
            public void onSuccess(List<MusicInfoEntity> o) {
                baseView.onSuccess(o);
            }

            @Override
            public void onError(String msg) {
                baseView.showError(msg);
            }
        });
    }

    @Override
    public void deleteMusic(MusicInfoEntity entity,int p) {
        addDisposable(dao.delete(entity), new RoomBaseCompletable() {
            @Override
            public void onSuccess() {
                baseView.onDeleteSuccess(p);
            }

            @Override
            public void onError(String msg) {
                baseView.showError(msg);
            }
        });
    }

}

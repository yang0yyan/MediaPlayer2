package com.yy.mediaplayer.net.presenter;



import com.yy.mediaplayer.net.BasePresenter;
import com.yy.mediaplayer.net.view.ScanRoomView;
import com.yy.mediaplayer.room.RoomBaseConsumer;
import com.yy.mediaplayer.room.dao.MusicInfoDao;
import com.yy.mediaplayer.room.entity.MusicInfoEntity;

import java.util.List;

public class ScanRoomPresenter extends BasePresenter<ScanRoomView.view> implements ScanRoomView.presenter {

    private final MusicInfoDao dao;

    public ScanRoomPresenter(ScanRoomView.view baseView) {
        super(baseView);
        dao = db.musicInfoDao();
    }


    @Override
    public void insert(List<MusicInfoEntity> infos) {
        addDisposable(dao.insertAll(infos), new RoomBaseConsumer<long[]>(baseView) {
            @Override
            public void onSuccess(long[] o) {
                baseView.onInsertSuccess(o);
            }

            @Override
            public void onError(String msg) {
                baseView.showError(msg);
            }
        });
    }
}

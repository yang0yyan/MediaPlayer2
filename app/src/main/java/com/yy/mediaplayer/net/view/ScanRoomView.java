package com.yy.mediaplayer.net.view;

import com.yy.mediaplayer.net.BaseView;
import com.yy.mediaplayer.room.entity.MusicInfoEntity;

import java.util.List;

public interface ScanRoomView extends BaseView {
    interface view extends BaseView {
        void onInsertSuccess(long[] ls);
    }

    interface presenter {
        void insert(List<MusicInfoEntity> infos);
    }
}

package com.yy.mediaplayer.net.view;

import com.yy.mediaplayer.net.BaseView;
import com.yy.mediaplayer.room.entity.MusicInfoEntity;

import java.util.List;

public interface LocalMusicView {

    interface view extends BaseView {
        void onSuccess(List<MusicInfoEntity> listMusicInfo);
        void onDeleteSuccess(int p);
    }

    interface presenter {
        void getMusic();
        void deleteMusic(MusicInfoEntity entity,int p);
    }
}

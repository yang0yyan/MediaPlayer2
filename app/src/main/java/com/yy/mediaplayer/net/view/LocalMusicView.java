package com.yy.mediaplayer.net.view;

import com.yy.mediaplayer.net.BaseView;
import com.yy.mediaplayer.room.entity.MusicInfoEntity;

import java.util.List;

public interface LocalMusicView {

    interface view extends BaseView {
        void onSuccess(List<MusicInfoEntity> listMusicInfo);
        void onUpdateSuccess(int p);
        void onDeleteSuccess(int p);
    }

    interface presenter {
        void getMusic();
        void getMusicByFilename(String filename);
        void getMusicCollection(boolean isCollection);
        void updateMusic(MusicInfoEntity infos,int p);
        void deleteMusic(MusicInfoEntity entity,int p);
    }
}

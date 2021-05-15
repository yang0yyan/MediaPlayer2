package com.yy.mediaplayer.model;

import com.yy.mediaplayer.room.entity.MusicInfoEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicQueueManager {
    private static MusicQueueManager instance;
    private int position;

    private MusicQueueManager() {

    }

    public static MusicQueueManager getInstance() {
        if (null == instance) {
            synchronized (MusicQueueManager.class) {
                if (null == instance) {
                    instance = new MusicQueueManager();
                }
            }
        }
        return instance;
    }

    private List<MusicInfoEntity> listMusicInfo = new ArrayList<>();
    private Map<String, MusicInfoEntity> mapMusicInfo = new HashMap<>();

    public List<MusicInfoEntity> getListMusicInfo() {
        return listMusicInfo;
    }

    public void setListMusicInfo(List<MusicInfoEntity> listMusicInfo) {
        if (this.listMusicInfo.containsAll(listMusicInfo)) return;
        this.listMusicInfo = listMusicInfo;
        mapMusicInfo.clear();
        for (MusicInfoEntity musicInfo : listMusicInfo) {
            mapMusicInfo.put(musicInfo.getId(), musicInfo);
        }
    }

    public MusicInfoEntity getMusicInfo(String id) {
        MusicInfoEntity musicInfo = mapMusicInfo.get(id);
        position = listMusicInfo.indexOf(musicInfo);
        return musicInfo;
    }

    public MusicInfoEntity skipMusic(int num) {
        if(null==listMusicInfo||listMusicInfo.size()==0)return null;
        position += num;
        if (position < 0)
            position = listMusicInfo.size() - 1;
        if (position >= listMusicInfo.size())
            position = 0;
        return listMusicInfo.get(position);
    }
}

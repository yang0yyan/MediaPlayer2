package com.yy.mediaplayer.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.yy.mediaplayer.room.dao.MusicInfoDao;
import com.yy.mediaplayer.room.entity.MusicInfoEntity;


@Database(entities = {MusicInfoEntity.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MusicInfoDao musicInfoDao();
}

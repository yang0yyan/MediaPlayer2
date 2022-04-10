package com.yy.mediaplayer.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.yy.mediaplayer.room.dao.MusicInfoDao;
import com.yy.mediaplayer.room.dao.UserInfoDao;
import com.yy.mediaplayer.room.entity.MusicInfoEntity;
import com.yy.mediaplayer.room.entity.UserInfoEntity;


@Database(entities = {MusicInfoEntity.class, UserInfoEntity.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MusicInfoDao musicInfoDao();
    public abstract UserInfoDao userInfoDao();
}

package com.yy.mediaplayer.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.yy.mediaplayer.room.dao.MusicInfoDao;
import com.yy.mediaplayer.room.dao.UserDao;
import com.yy.mediaplayer.room.entity.MusicInfoEntity;
import com.yy.mediaplayer.room.entity.UserEntity;


@Database(entities = {UserEntity.class, MusicInfoEntity.class }, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();

    public abstract MusicInfoDao musicInfoDao();
}

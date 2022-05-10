package com.yy.mediaplayer.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.yy.mediaplayer.room.entity.UserInfoEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

@Dao
public interface UserInfoDao {

    @Insert()
    Completable insert(UserInfoEntity info);

    @Query("SELECT * FROM user_info where userName = :username")
    Flowable<List<UserInfoEntity>> getUser(String username);
}
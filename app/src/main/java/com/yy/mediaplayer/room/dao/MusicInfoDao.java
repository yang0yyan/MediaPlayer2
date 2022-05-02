package com.yy.mediaplayer.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.yy.mediaplayer.room.entity.MusicInfoEntity;
import com.yy.mediaplayer.room.entity.UserInfoEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface MusicInfoDao {

    @Query("SELECT * FROM music_info ORDER BY name ASC")
    Flowable<List<MusicInfoEntity>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<long[]> insertAll(List<MusicInfoEntity> infos);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Completable update(MusicInfoEntity info);

    @Delete
    Completable delete(MusicInfoEntity users);

    @Query("SELECT * FROM music_info where isCollection = :status")
    Flowable<List<MusicInfoEntity>> getMusicCollection(boolean status);

    @Query("SELECT * FROM music_info where file_name like '%' || :filename || '%' or artist like '%' || :filename || '%'")
    Flowable<List<MusicInfoEntity>> getMusicByFilename(String filename);
}

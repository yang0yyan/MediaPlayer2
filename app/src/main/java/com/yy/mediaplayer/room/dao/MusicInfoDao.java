package com.yy.mediaplayer.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.yy.mediaplayer.room.entity.MusicInfoEntity;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface MusicInfoDao {

    @Query("SELECT * FROM music_info ORDER BY name ASC")
    Flowable<List<MusicInfoEntity>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<long[]> insertAll(List<MusicInfoEntity> infos);
}

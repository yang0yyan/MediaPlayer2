package com.yy.mediaplayer.room.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import java.io.Serializable;

@Entity(tableName = "music_info", primaryKeys = {"file_path"})
public class MusicInfoEntity implements Serializable {

    @Ignore
    public MusicInfoEntity() {
    }

    public MusicInfoEntity(@NonNull String id, @NonNull String fileName, @NonNull String filePath, String name, String album, String artist, String bitrate, long duration, String imageUrl, boolean isCollection) {
        this.id = id;
        this.fileName = fileName;
        this.filePath = filePath;
        this.name = name;
        this.album = album;
        this.artist = artist;
        this.bitrate = bitrate;
        this.duration = duration;
        this.imageUrl = imageUrl;
        this.isCollection = isCollection;
    }


    @NonNull
    private String id;

    @NonNull
    @ColumnInfo(name = "file_name")
    private String fileName;

    @NonNull
    @ColumnInfo(name = "file_path")
    private String filePath;

    private String name;
    private String album;
    private String artist;
    private String bitrate;
    private long duration;
    private String imageUrl;
    @ColumnInfo(defaultValue = "0")
    private boolean isCollection;

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getBitrate() {
        return bitrate;
    }

    public void setBitrate(String bitrate) {
        this.bitrate = bitrate;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public void setCollection(boolean collection) {
        isCollection = collection;
    }
}

package com.yy.mediaplayer.net;

import com.yy.mediaplayer.net.bean.BaseModel;
import com.yy.mediaplayer.net.bean.MusicBean;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface ApiServer {

    @GET("https://storage.googleapis.com/automotive-media/music.json")
    Observable<BaseModel<MusicBean>> getMusic();
}
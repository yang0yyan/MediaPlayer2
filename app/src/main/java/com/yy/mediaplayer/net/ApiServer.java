package com.yy.mediaplayer.net;

import com.yy.mediaplayer.net.bean.MusicBean;

import java.util.HashMap;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiServer {

    @GET("https://storage.googleapis.com/automotive-media/music.json")
    Observable<BaseModel<MusicBean>> getMusic();
}
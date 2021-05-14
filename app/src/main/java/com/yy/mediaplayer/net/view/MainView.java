package com.yy.mediaplayer.net.view;

import com.yy.mediaplayer.net.BaseView;
import com.yy.mediaplayer.net.bean.MusicBean;

import java.util.List;

public interface MainView {

    interface view extends BaseView {
        void onSuccess(List<MusicBean> list);
    }

    interface presenter {
        void getMusicList();
    }
}

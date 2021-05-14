package com.yy.mediaplayer.model.media;

public interface Playback {
    interface Callback {
        void onPlaybackStatusChanged(int state);
    }

    void setCallback(Callback callback);
}

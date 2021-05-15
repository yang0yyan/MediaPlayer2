package com.yy.mediaplayer.model.media;

public interface Playback {
    interface Callback {
        void onPlaybackStatusChanged(int state);
        void onPlayCompletionChanged();
    }

    void setCallback(Callback callback);
}

package com.yy.mediaplayer.base;

import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.NonNull;

import com.yy.mediaplayer.utils.LogHelper;

public abstract class BaseMediaFragment extends BaseFragment {
    private static final String TAG = LogHelper.makeLogTag(BaseMediaFragment.class);

    private final MediaControllerCompat.Callback mCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
            mPlaybackState = state;
            if(null==mPlaybackState)return;
            BaseMediaFragment.this.onPlaybackStateChanged(state);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            mMediaMetadata = metadata;
            if(null==mMediaMetadata)return;
            mMediaDescription = mMediaMetadata.getDescription();
            if(null==mMediaDescription)return;
            BaseMediaFragment.this.onMetadataChanged(metadata);
        }
    };
    public MediaMetadataCompat mMediaMetadata;
    public PlaybackStateCompat mPlaybackState;
    public MediaDescriptionCompat mMediaDescription;


    public void onConnected() {
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(getActivity());
        LogHelper.d(TAG, "onConnected, mediaController==null? ", controller == null);
        if (controller != null) {
            mCallback.onMetadataChanged(controller.getMetadata());
            mCallback.onPlaybackStateChanged(controller.getPlaybackState());
            controller.registerCallback(mCallback);
        }
    }

    protected void onMetadataChanged(MediaMetadataCompat metadata) {
    }

    protected void onPlaybackStateChanged(PlaybackStateCompat state) {
    }
}

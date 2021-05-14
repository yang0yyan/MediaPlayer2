package com.yy.mediaplayer.base;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yy.mediaplayer.service.MediaService;
import com.yy.mediaplayer.utils.LogHelper;

public abstract class BaseMediaActivity extends BaseActivity {
    private static final String TAG = LogHelper.makeLogTag(BaseMediaActivity.class);
    private MediaBrowserCompat mediaBrowser;
    public MediaMetadataCompat mMediaMetadata;
    public PlaybackStateCompat mPlaybackState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaBrowser = new MediaBrowserCompat(this, new ComponentName(this, MediaService.class), connectionCallback, null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mediaBrowser.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaBrowser.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void connectToSession(MediaSessionCompat.Token token) throws RemoteException {
        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(this);
        if (mediaController == null) {
            mediaController = new MediaControllerCompat(this, token);
            MediaControllerCompat.setMediaController(this, mediaController);
        }
        mediaController.registerCallback(mMediaControllerCallback);
        mMediaMetadata = mediaController.getMetadata();
        mPlaybackState = mediaController.getPlaybackState();

        onMediaControllerConnected();
    }

    protected void onMediaControllerConnected() {
        // empty implementation, can be overridden by clients.
    }


    private final MediaBrowserCompat.ConnectionCallback connectionCallback = new MediaBrowserCompat.ConnectionCallback() {
        @Override
        public void onConnected() {
            super.onConnected();
            LogHelper.d(TAG, "onConnected");
            try {
                connectToSession(mediaBrowser.getSessionToken());
            } catch (RemoteException e) {
                LogHelper.e(TAG, e, "could not connect media controller");
            }
        }
    };

    private final MediaControllerCompat.Callback mMediaControllerCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
                    LogHelper.d(TAG, "onPlaybackStateChanged: ");
                    mPlaybackState = state;
                }

                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    LogHelper.d(TAG, "onMetadataChanged: ");
                    mMediaMetadata = metadata;
                }
            };
}

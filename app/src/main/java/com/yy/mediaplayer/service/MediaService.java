package com.yy.mediaplayer.service;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;

import com.yy.mediaplayer.activity.MainActivity;
import com.yy.mediaplayer.model.media.MediaPlayerManager;
import com.yy.mediaplayer.model.media.MusicProvider;
import com.yy.mediaplayer.notification.MediaNotificationManager;

import java.util.List;

public class MediaService extends MediaBrowserServiceCompat {


    public static final String MEDIA_ID_EMPTY_ROOT = "__EMPTY_ROOT__";
    public static final String MEDIA_ID_ROOT = "__ROOT__";

    private String LOG_TAG = "媒体会话";
    private MediaSessionCompat mediaSession;
    private MusicProvider musicProvider;
    private MediaPlayerManager mediaPlayer;
    private MediaNotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();

        mediaSession = new MediaSessionCompat(this, LOG_TAG);
        setSessionToken(mediaSession.getSessionToken());

        Context context = getApplicationContext();
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 99 /*request code*/,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mediaSession.setSessionActivity(pi);

        mediaPlayer = new MediaPlayerManager(this);
        notificationManager = new MediaNotificationManager(this);
        musicProvider = new MusicProvider(mediaPlayer, notificationManager, mediaSession);

        mediaSession.setCallback(musicProvider.mediaSessionCallback);
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return new BrowserRoot(MEDIA_ID_ROOT, null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mediaPlayer.release();
        notificationManager.stopNotification();
        mediaSession.release();
    }
}

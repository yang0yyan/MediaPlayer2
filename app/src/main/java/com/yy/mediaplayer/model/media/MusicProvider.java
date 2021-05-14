package com.yy.mediaplayer.model.media;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.yy.mediaplayer.model.MusicQueueManager;
import com.yy.mediaplayer.notification.MediaNotificationManager;
import com.yy.mediaplayer.room.entity.MusicInfoEntity;

public class MusicProvider implements Playback.Callback {
    private final MediaMetadataCompat.Builder mediaMetadataBuilder;
    private final PlaybackStateCompat.Builder stateBuilder;
    private MediaPlayerManager mediaPlayer;
    private MediaNotificationManager notificationManager;
    private MediaSessionCompat mediaSession;
    private final MediaControllerCompat mediaController;
    private final MediaMetadataCompat mediaMetadata;

    private MusicInfoEntity musicInfo;

    public MusicProvider(MediaPlayerManager mediaPlayer, MediaNotificationManager notificationManager, MediaSessionCompat mediaSession) {
        this.mediaPlayer = mediaPlayer;
        this.notificationManager = notificationManager;
        this.mediaSession = mediaSession;
        mediaController = mediaSession.getController();
        mediaMetadata = mediaController.getMetadata();
        mediaMetadataBuilder = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, "123")
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, "12321")
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, "12321313");
        stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_PLAY);
        mediaPlayer.setCallback(this);
        onPlaybackStatusChanged(0);
    }

    public final MediaSessionCompat.Callback mediaSessionCallback = new MediaSessionCompat.Callback() {


        @Override
        public void onPlay() {
            mediaPlayer.start();
//            notificationManager.startNotification();
        }

        @Override
        public void onSeekTo(long pos) {
            mediaPlayer.seek(pos);
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            musicInfo = MusicQueueManager.getInstance().getMusicInfo(mediaId);
            playMusic();
        }

        @Override
        public void onPause() {
            mediaPlayer.pause();
//            notificationManager.stopNotification();
        }

        @Override
        public void onStop() {
            mediaPlayer.stop();
            mediaSession.setActive(true);
//            notificationManager.stopNotification();
        }

        @Override
        public void onSkipToPrevious() {
            skipMusic(-1);
            playMusic();
        }

        @Override
        public void onSkipToNext() {
            skipMusic(1);
            playMusic();
        }
    };

    private void playMusic() {
        mediaPlayer.prepareAndPlay(musicInfo.getFilePath());
        mediaSession.setActive(true);
        updateMetadata();
        notificationManager.startNotification();
    }

    public void updateMetadata() {
        mediaMetadataBuilder
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, musicInfo.getName())
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, musicInfo.getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, musicInfo.getAlbum())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, musicInfo.getDuration());
        mediaSession.setMetadata(mediaMetadataBuilder.build());
    }


    private void skipMusic(int num) {
        musicInfo = MusicQueueManager.getInstance().skipMusic(num);
    }

    @Override
    public void onPlaybackStatusChanged(int state) {
        stateBuilder.setActions(getAvailableActions());
        long position = mediaPlayer.getCurrentPosition();
        stateBuilder.setState(state, position, 1.0f, SystemClock.elapsedRealtime());
        mediaSession.setPlaybackState(stateBuilder.build());
        mediaSession.getController().getPlaybackState().getState();
    }

    private void setCustomAction(PlaybackStateCompat.Builder stateBuilder) {

    }

    private long getAvailableActions() {
        long actions =
                PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID |
                        PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                        PlaybackStateCompat.ACTION_SEEK_TO;
        if (mediaPlayer.status == MediaPlayerManager.Status.STARTED) {
            actions |= PlaybackStateCompat.ACTION_PAUSE;
        } else {
            actions |= PlaybackStateCompat.ACTION_PLAY;
        }
        return actions;
    }


}

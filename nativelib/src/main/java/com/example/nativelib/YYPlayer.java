package com.example.nativelib;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.HardwareBuffer;
import android.media.ImageReader;
import android.net.Uri;
import android.view.Surface;

import com.example.nativelib.iface.IPlayer;

public class YYPlayer implements IPlayer, YYMediaCodec.MediaDecodeListener {

    Context context;
    private YYMediaCodec mediaCodec;
    private AudioTrackManager audioTrackManager;
    private YYCamera camera;

    public YYPlayer(Context context) {
        this.context = context;
    }

    public void init() {
        mediaCodec = new YYMediaCodec(context);
        mediaCodec.setMediaDecodeListener(this);
        mediaCodec.init();
        audioTrackManager = new AudioTrackManager();

        camera = new YYCamera(context);
        camera.setMediaDecodeListener(this);
        camera.init();
    }

    @Override
    public void readMediaFile(Uri uri) {
        mediaCodec.readMediaFile(uri);
    }

    @Override
    public void startPlay() {
        mediaCodec.startDecode();
        audioTrackManager.play();
    }


    @Override
    public void setSurface(Surface surface) {
        IMediaPlayer.setSurface(surface);
    }

    @Override
    public void removeSurface() {
        IMediaPlayer.removeSurface();
    }

    @Override
    public void release() {
        mediaCodec.release();
        audioTrackManager.release();
        camera.release();
    }


    public void opC(){
        camera.open();
    }



    private MediaStatusListener listener;

    public void setMediaStatusListener(MediaStatusListener listener) {
        this.listener = listener;
    }


    @Override
    public void readAudioComplete(int sampleRateInHz, int channelConfig, int audioFormat) {
        audioTrackManager.init(sampleRateInHz, channelConfig, audioFormat);
    }

    @Override
    public void readVideoComplete(int width, int height, int rotation, long time) {
        listener.onVideoInfo(width, height, time);
        IMediaPlayer.initVideo(width, height, rotation);
    }

    @Override
    public void onVideoOutput(byte[] bytes) {
        IMediaPlayer.videoWrite(bytes);
    }

    @Override
    public void onAudioOutput(byte[] bytes) {
        audioTrackManager.write(bytes);
    }

    @Override
    public void logMediaInfo(String msg) {
        listener.logMediaInfo(msg);
    }

    public static interface MediaStatusListener {
        void onVideoInfo(int width, int height, long time);

        void onPlayStatusChanged(boolean status);

        void onProgressChanged(long us);

        void logMediaInfo(String msg);
    }
}

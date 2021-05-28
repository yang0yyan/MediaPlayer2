package com.yy.mediaplayer.model.video;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.NonNull;

import com.yy.mediaplayer.activity.VideoPlayActivity;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;

import static android.os.SystemClock.sleep;

//MediaCodec、MediaExtractor、MediaSync、MediaMuxer、MediaCrypto、MediaDrm、Image、Surface和AudioTrack
public class MediaCodecManager2 {
    private static final String TAG = "MediaCodec";
    private final VideoPlayActivity activity;
    private final Context context;
    private final Surface surface;
    private Uri fileUri = null;

    private final HashMap<String, MediaCodecInfo.CodecCapabilities> videoDecoderInfos = new HashMap<>();
    private final HashMap<String, MediaCodecInfo.CodecCapabilities> audioDecoderInfos = new HashMap<>();

    private int videoWidth;
    private int videoHeight;
    private int videoSampleRateInHz;

    private int sampleRateInHz;
    private int channelConfig;
    private int audioFormat;
    private int bufferSizeInBytes;

    private int audioIndex = -1;
    private int videoIndex = -1;
    private MediaExtractor videoMediaExtractor;
    private MediaExtractor audioMediaExtractor;
    private MediaCodec videoMediaCodec;
    private MediaCodec audioMediaCodec;
    private ChildThread videoThread;
    private ChildThread audioThread;

    private AudioTrackManager audioTrackManager;
    private Status mediaStatus = Status.INIT;

    private enum Status {
        INIT(0), PLAYING(1), PAUSE(2), STOP(3), ERROR(-1);

        int value;

        Status(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private boolean isRun;
    private boolean isStop = false;
    private boolean isPause = false;
    private long startMs = 0;


    public MediaCodecManager2(Activity activity, Surface surface) {
        this.activity = (VideoPlayActivity) activity;
        this.context = activity;
        this.surface = surface;
        isRun = true;
        audioTrackManager = new AudioTrackManager(this.activity);
        getMediaCodecList();
    }

    public void init(final Uri uri) {
        fileUri = uri;
        new Thread(new Runnable() {
            @Override
            public void run() {
                initMedia(fileUri);
            }
        }).start();
    }

    private void initMedia(Uri uri) {
        if (null == uri) return;
        isStop = true;
        while (null != videoMediaCodec || null != videoMediaExtractor || null != audioMediaCodec || null != audioMediaExtractor)
            sleep(100);
        isStop = false;
        if (null == videoThread) {
            videoThread = new ChildThread();
            videoThread.start();
        }
        if (null == audioThread) {
            audioThread = new ChildThread();
            audioThread.start();
        }

        if (mediaStatus == Status.INIT) {
            releaseAudio();
            releaseVideo();
        }
        MediaExtractor mediaExtractor = new MediaExtractor();

        try {
            mediaExtractor.setDataSource(context, uri, null);
            int trackCount = mediaExtractor.getTrackCount();
            activity.logFromUi("轨道数: " + trackCount);

            for (int i = 0; i < trackCount; i++) {
                MediaFormat mediaFormat = mediaExtractor.getTrackFormat(i);
                String mime = mediaFormat.getString(MediaFormat.KEY_MIME);

                if (mime.startsWith("video/")) {
                    if (videoDecoderInfos.get(mime) == null) {
                        activity.logFromUi("格式不支持: " + mime);
                        continue;
                    }
                    videoIndex = i;
                    activity.logFromUi("视频mime-type: " + mime);

                    int rotation = 0;
                    try {
                        rotation = mediaFormat.getInteger(MediaFormat.KEY_ROTATION);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    videoSampleRateInHz = mediaFormat.getInteger(MediaFormat.KEY_FRAME_RATE);
                    videoWidth = mediaFormat.getInteger(MediaFormat.KEY_WIDTH);
                    videoHeight = mediaFormat.getInteger(MediaFormat.KEY_HEIGHT);
                    if (rotation == 0) {
                        activity.setSurfaceViewLayoutParams(videoWidth, videoHeight);
                    } else if (rotation == 90) {
                        activity.setSurfaceViewLayoutParams(videoHeight, videoWidth);
                    }
                    activity.logFromUi("videoSampleRateInHz: " + videoSampleRateInHz + "; videoWidth:" + videoWidth + "; videoHeight:" + videoHeight + "; rotation:" + rotation);
                    if (null == videoMediaExtractor)
                        videoMediaExtractor = new MediaExtractor();

                    videoMediaExtractor.setDataSource(context, uri, null);
                    videoMediaExtractor.selectTrack(i);
                    videoMediaCodec = MediaCodec.createDecoderByType(mime);
                    videoMediaCodec.setCallback(videoCallback, new Handler(videoThread.childLooper));
                    videoMediaCodec.configure(mediaFormat, surface, null, 0);
//                    MediaPlayer.init(surface,videoWidth,videoHeight);

                } else if (mime.startsWith("audio/")) {
                    if (audioDecoderInfos.get(mime) == null) {
                        activity.logFromUi("格式不支持: " + mime);
                        continue;
                    }
                    audioIndex = i;
                    activity.logFromUi("音频mime-type: " + mime);
                    sampleRateInHz = mediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
                    int channelCount = mediaFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
                    if (channelCount == 1) {
                        channelConfig = AudioFormat.CHANNEL_OUT_MONO;
                    } else {
                        channelConfig = AudioFormat.CHANNEL_OUT_STEREO;
                    }
                    activity.logFromUi("sampleRateInHz: " + sampleRateInHz + "; channelConfig:" + channelConfig + "; audioFormat:" + audioFormat);
                    if (null == audioMediaExtractor)
                        audioMediaExtractor = new MediaExtractor();

                    audioMediaExtractor.setDataSource(context, uri, null);
                    audioMediaExtractor.selectTrack(i);
                    audioMediaCodec = MediaCodec.createDecoderByType(mime);
                    audioMediaCodec.setCallback(audioCallback, new Handler(audioThread.childLooper));
                    audioMediaCodec.configure(mediaFormat, null, null, 0);
                }

            }
            mediaStatus = Status.INIT;
            play();
        } catch (IOException e) {
            e.printStackTrace();
            mediaStatus = Status.ERROR;
            releaseAudio();
            releaseAudio();
            Log.e(TAG, "initMedia: ", e);
        } finally {
            mediaExtractor.release();

        }

    }

    public void play() {
        switch (mediaStatus) {
            case INIT:
                startPlay();
                break;
            case PLAYING:
                isStop = true;
                //isPause = true;
                //mediaStatus = Status.PAUSE;
                break;
            case PAUSE:
                isPause = false;
                mediaStatus = Status.PLAYING;
                break;
        }

    }

    private void startPlay() {
        if (videoIndex != -1) {
            startMs = System.currentTimeMillis();
            videoMediaCodec.start();
        }
        if (audioIndex != -1) {
            if (null == audioTrackManager)
                audioTrackManager = new AudioTrackManager(activity);
            audioTrackManager.init(sampleRateInHz, channelConfig, AudioFormat.ENCODING_PCM_16BIT);
            audioTrackManager.play();
            audioMediaCodec.start();
        }
        mediaStatus = Status.PLAYING;
    }

    /**
     * ----------------------------------------------------------------------------------------
     */
    private MediaCodec.Callback videoCallback = new MediaCodec.Callback() {
        byte[] data;
        private long pauseTime;

        @Override
        public void onInputBufferAvailable(@NonNull MediaCodec codec, int inIndex) {
            ByteBuffer inBuffer = codec.getInputBuffer(inIndex);
            int size = videoMediaExtractor.readSampleData(inBuffer, 0);
            if (size < 0 || !isRun || isStop) {
                codec.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
            } else {
                codec.queueInputBuffer(inIndex, 0, size, videoMediaExtractor.getSampleTime(), 0);
                videoMediaExtractor.advance();
                inBuffer.clear();
            }
        }

        @Override
        public void onOutputBufferAvailable(@NonNull MediaCodec codec, int outIndex, @NonNull MediaCodec.BufferInfo outBufferInfo) {
            if ((outBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0 || isStop) {
                codec.releaseOutputBuffer(outIndex, true);
                releaseVideo();
                Log.d(TAG, "run: 视频  结束");
                return;
            }
            if (isPause)
                pauseTime = System.currentTimeMillis();
            while (isPause)
                sleep(100);
            if (outBufferInfo.presentationTimeUs / 1000 > System.currentTimeMillis() - startMs) {
                sleep(outBufferInfo.presentationTimeUs / 1000 - (System.currentTimeMillis() - startMs));
            }
//            if(outBufferInfo.size>0){
//                ByteBuffer outBuffer = codec.getOutputBuffer(outIndex);
//                outBuffer.position(outBufferInfo.offset);
//                outBuffer.limit(outBufferInfo.offset + outBufferInfo.size);
//                if (data == null)
//                    data = new byte[outBufferInfo.size];
//                Arrays.fill(data, (byte) 0);
//                outBuffer.get(data);
//                MediaPlayer.write(data);
//            }

            codec.releaseOutputBuffer(outIndex, true);
        }

        @Override
        public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
            Log.d(TAG, "onError: 错误1");
            releaseVideo();
            Log.d(TAG, "run: 视频  结束");
        }

        @Override
        public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
            Log.d(TAG, "onOutputFormatChanged: ");
        }
    };
    /**
     * -------------------------------------------------------------------------------------------
     */
    private MediaCodec.Callback audioCallback = new MediaCodec.Callback() {

        private byte[] data;

        @Override
        public void onInputBufferAvailable(@NonNull MediaCodec codec, int inIndex) {
            ByteBuffer inBuffer = codec.getInputBuffer(inIndex);
            int size = audioMediaExtractor.readSampleData(inBuffer, 0);
            if (size < 0 || !isRun || isStop) {
                Log.d(TAG, "mybe eos or error");
                codec.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
            } else {
                codec.queueInputBuffer(inIndex, 0, size, audioMediaExtractor.getSampleTime(), 0);
                audioMediaExtractor.advance();
                inBuffer.clear();
            }

        }

        @Override
        public void onOutputBufferAvailable(@NonNull MediaCodec codec, int outIndex, @NonNull MediaCodec.BufferInfo outBufferInfo) {
            if ((outBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0 || isStop) {
                codec.releaseOutputBuffer(outIndex, true);
                releaseAudio();
                Log.d(TAG, "run: 音频  结束");
                return;
            }
            while (isPause)
                sleep(100);
            if (outBufferInfo.size > 0) {
                ByteBuffer outBuffer = codec.getOutputBuffer(outIndex);
                outBuffer.position(outBufferInfo.offset);
                outBuffer.limit(outBufferInfo.offset + outBufferInfo.size);
                if (data == null)
                    data = new byte[outBufferInfo.size];
                Arrays.fill(data, (byte) 0);
                outBuffer.get(data);
                audioTrackManager.onAudioFrame(data);//output to audio track
                outBuffer.clear();
            }
            codec.releaseOutputBuffer(outIndex, false);
        }

        @Override
        public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
            Log.d(TAG, "onError2: ");
            releaseAudio();
            Log.d(TAG, "run: 音频  结束");
        }

        @Override
        public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
            Log.d(TAG, "onOutputFormatChanged2: ");
        }
    };

    private void getMediaCodecList() {
        videoDecoderInfos.clear();
        audioDecoderInfos.clear();
        MediaCodecList mediaCodecList = new MediaCodecList(MediaCodecList.ALL_CODECS);
        MediaCodecInfo[] mediaCodecInfos = mediaCodecList.getCodecInfos();
        for (int i = mediaCodecInfos.length - 1; i >= 0; i--) {
            MediaCodecInfo codecInfo = mediaCodecInfos[i];
            if (!codecInfo.isEncoder()) {
                for (String t : codecInfo.getSupportedTypes()) {
                    if (t.startsWith("video/")) {
                        videoDecoderInfos.put(t, codecInfo.getCapabilitiesForType(t));
                    } else if (t.startsWith("audio/")) {
                        audioDecoderInfos.put(t, codecInfo.getCapabilitiesForType(t));
                    }
                }
            }
        }
        Log.d(TAG, "getMediaCodecList: " + videoDecoderInfos.toString());
        Log.d(TAG, "getMediaCodecList: " + audioDecoderInfos.toString());
    }

    private static class ChildThread extends Thread {
        Looper childLooper;

        @Override
        public void run() {
            super.run();
            Looper.prepare();
            childLooper = Looper.myLooper();
            Looper.loop();
        }
    }


    public void release() {
        isRun = false;
        if (null != videoThread && videoThread.isAlive()) {
            videoThread.interrupt();
        }
        if (null != audioThread && audioThread.isAlive()) {
            audioThread.interrupt();
        }
        if (mediaStatus == Status.INIT) {
            releaseVideo();
            releaseAudio();
        }
    }

    private void releaseVideo() {
        mediaStatus = Status.STOP;
        if (null != videoMediaExtractor) {
            videoMediaExtractor.release();
            videoMediaExtractor = null;
        }
        if (null != videoMediaCodec) {
            videoMediaCodec.stop();
            videoMediaCodec.release();
            videoMediaCodec = null;
        }
        Log.d(TAG, "close: 资源释放  视频");
    }

    private void releaseAudio() {
        mediaStatus = Status.STOP;
        if (null != audioMediaCodec) {
            audioMediaCodec.stop();
            audioMediaCodec.release();
            audioMediaCodec = null;
        }
        if (null != audioMediaExtractor) {
            audioMediaExtractor.release();
            audioMediaExtractor = null;
        }
        if (!isRun && null != audioTrackManager)
            audioTrackManager.release();
        else if (null != audioTrackManager)
            audioTrackManager.stop();
        Log.d(TAG, "close: 资源释放  音频");
    }
}

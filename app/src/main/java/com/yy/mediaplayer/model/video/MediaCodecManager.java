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
import android.os.SystemClock;
import android.util.Log;
import android.view.Surface;

import com.yy.mediaplayer.activity.VideoPlayActivity;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;

import static android.os.SystemClock.sleep;

//MediaCodec、MediaExtractor、MediaSync、MediaMuxer、MediaCrypto、MediaDrm、Image、Surface和AudioTrack
public class MediaCodecManager {
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
    private Thread videoThread;
    private Thread audioThread;

    private AudioTrackManager audioTrackManager;


    public MediaCodecManager(Activity activity, Surface surface) {
        this.activity = (VideoPlayActivity) activity;
        this.context = activity;
        this.surface = surface;
        isRun = true;
        audioTrackManager = new AudioTrackManager(this.activity);
        getMediaCodecList();
    }

    public void start(Uri uri) {
        fileUri = uri;
        new Thread(stopRunnable).start();
    }

    public void initMedia(Uri uri) {
        if (null == uri) return;

        MediaExtractor mediaExtractor = new MediaExtractor();

        try {
            mediaExtractor.setDataSource(context, uri, null);
            int trackCount = mediaExtractor.getTrackCount();
            activity.logFromUi("轨道数:" + trackCount);

            for (int i = 0; i < trackCount; i++) {
                MediaFormat mediaFormat = mediaExtractor.getTrackFormat(i);
                String mime = mediaFormat.getString(MediaFormat.KEY_MIME);

                if (mime.startsWith("video/")) {
                    if (videoDecoderInfos.get(mime) == null) {
                        activity.logFromUi("格式不支持:" + mime);
                        continue;
                    }
                    videoIndex = i;
                    activity.logFromUi("视频mime-type:" + mime);
                    int rotation = 0;//mediaFormat.getInteger(MediaFormat.KEY_ROTATION);
                    videoSampleRateInHz = mediaFormat.getInteger(MediaFormat.KEY_FRAME_RATE);
                    videoWidth = mediaFormat.getInteger(MediaFormat.KEY_WIDTH);
                    videoHeight = mediaFormat.getInteger(MediaFormat.KEY_HEIGHT);
                    if (rotation == 0) {
                        activity.setSurfaceViewLayoutParams(videoWidth, videoHeight);
                    } else if (rotation == 90) {
                        activity.setSurfaceViewLayoutParams(videoHeight, videoWidth);
                    }
                    activity.logFromUi("videoSampleRateInHz:" + videoSampleRateInHz + ";videoWidth:" + videoWidth + ";videoHeight:" + videoHeight + ";rotation:" + rotation);
                    if (null == videoMediaExtractor)
                        videoMediaExtractor = new MediaExtractor();

                    videoMediaExtractor.setDataSource(context, uri, null);
                    videoMediaExtractor.selectTrack(i);
                    videoMediaCodec = MediaCodec.createDecoderByType(mime);
                    videoMediaCodec.configure(mediaFormat, surface, null, 0);

                } else if (mime.startsWith("audio/")) {
                    if (audioDecoderInfos.get(mime) == null) {
                        activity.logFromUi("格式不支持:" + mime);
                        continue;
                    }
                    audioIndex = i;
                    activity.logFromUi("音频mime-type:" + mime);
                    sampleRateInHz = mediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE) / 2;
                    channelConfig = mediaFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT) / 2;
                    activity.logFromUi("sampleRateInHz:" + sampleRateInHz + ";channelConfig:" + channelConfig + ";audioFormat:" + audioFormat);
                    if (null == audioMediaExtractor)
                        audioMediaExtractor = new MediaExtractor();

                    audioMediaExtractor.setDataSource(context, uri, null);
                    audioMediaExtractor.selectTrack(i);
                    audioMediaCodec = MediaCodec.createDecoderByType(mime);
                    audioMediaCodec.configure(mediaFormat, null, null, 0);
                }

            }
            startPlay();
        } catch (IOException e) {
            e.printStackTrace();
            close();
        } finally {
            mediaExtractor.release();

        }

    }

    public void getMediaCodecList() {
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

    public void startPlay() {
        if (videoIndex != -1) {
            videoThread = new Thread(videoRunnable);
            videoThread.start();
        }
        if (audioIndex != -1) {
            if (null == audioTrackManager)
                audioTrackManager = new AudioTrackManager(activity);
            audioTrackManager.init(sampleRateInHz, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
            audioThread = new Thread(audioRunnable);
            audioThread.start();
        }
    }

    boolean isEOS = false;
    boolean isRun = false;

    Runnable videoRunnable = new Runnable() {

        @Override
        public void run() {
            long timeout = 1000_000L / videoSampleRateInHz / 2;
            isEOS = false;
            long startMs = System.currentTimeMillis();
            MediaCodec.BufferInfo outBufferInfo = new MediaCodec.BufferInfo();
            videoMediaCodec.start();

            while (!isEOS) {
                int inIndex = videoMediaCodec.dequeueInputBuffer(timeout);
                if (inIndex >= 0) {
                    ByteBuffer inBuffer = videoMediaCodec.getInputBuffer(inIndex);
                    int size = videoMediaExtractor.readSampleData(inBuffer, 0);
                    if (size < 0) {
                        videoMediaCodec.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    } else {
                        videoMediaCodec.queueInputBuffer(inIndex, 0, size, videoMediaExtractor.getSampleTime(), 0);
                        videoMediaExtractor.advance();
                        inBuffer.clear();
                    }
                }

                int outIndex;
                do {
                    outIndex = videoMediaCodec.dequeueOutputBuffer(outBufferInfo, timeout);
                    if ((outBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        isEOS = true;
                    }
                    if (outIndex > 0) {
                        while (outBufferInfo.presentationTimeUs / 1000 > System.currentTimeMillis() - startMs) {
                            sleep(10);
                        }
                        if (isEOS) break;
                        videoMediaCodec.releaseOutputBuffer(outIndex, true);
                    }
                } while (outIndex >= 0);
            }
            videoMediaCodec.stop();
            releaseVideo();
            Log.d(TAG, "run: 视频  结束");
        }
    };


    Runnable audioRunnable = new Runnable() {
        @Override
        public void run() {
            long timeout = 1000L;
            byte[] data = null;
            MediaCodec.BufferInfo outBufferInfo = new MediaCodec.BufferInfo();
            audioMediaCodec.start();
            audioTrackManager.play();
            while (!isEOS) {
                int inIndex = audioMediaCodec.dequeueInputBuffer(timeout);
                if (inIndex >= 0) {
                    ByteBuffer inBuffer = audioMediaCodec.getInputBuffer(inIndex);
                    int size = audioMediaExtractor.readSampleData(inBuffer, 0);
                    if (size < 0) {
                        Log.d(TAG, "mybe eos or error");
                        audioMediaCodec.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    } else {
                        audioMediaCodec.queueInputBuffer(inIndex, 0, size, audioMediaExtractor.getSampleTime(), 0);
                        audioMediaExtractor.advance();
                        inBuffer.clear();
                    }
                }

                int outIndex;
                do {
                    outIndex = audioMediaCodec.dequeueOutputBuffer(outBufferInfo, timeout);
                    if ((outBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        Log.d(TAG, "outBufferInfo flag is BUFFER_FLAG_END_OF_STREAM");
                        isEOS = true;
                    }
                    if (outIndex >= 0) {
                        if (outBufferInfo.size > 0) {
                            ByteBuffer outBuffer = audioMediaCodec.getOutputBuffer(outIndex);
                            outBuffer.position(outBufferInfo.offset);
                            outBuffer.limit(outBufferInfo.offset + outBufferInfo.size);
                            if (data == null)
                                data = new byte[outBufferInfo.size];
                            Arrays.fill(data, (byte) 0);
                            outBuffer.get(data);
                            audioTrackManager.onAudioFrame(data);//output to audio track
                            outBuffer.clear();
                        }
                        audioMediaCodec.releaseOutputBuffer(outIndex, false);
                    }
                } while (outIndex >= 0);
            }
            audioTrackManager.stop();
            audioMediaCodec.stop();
            releaseAudio();
            Log.d(TAG, "run: 音频  结束");
        }
    };


    Runnable stopRunnable = new Runnable() {
        @Override
        public void run() {
            isEOS = true;
            int i = 0;
            while ((null != audioThread && audioThread.isAlive()) || (null != videoThread && videoThread.isAlive())) {
                i++;
                SystemClock.sleep(100);
                if (i > 50) {
                    Log.d(TAG, "run: 线程异常，强制销毁");
                    audioThread.interrupt();
                    videoThread.interrupt();
                    break;
                }
            }
            releaseVideo();
            releaseAudio();
            if (i < 50)
                initMedia(fileUri);
        }
    };

    public void release() {
        isEOS = true;
        isRun = false;
        if ((null == audioThread || !audioThread.isAlive()) && (null == videoThread || !videoThread.isAlive())) {
            if (null != audioTrackManager)
                audioTrackManager.release();
        }
    }

    public void close() {
        releaseAudio();
        releaseAudio();
        Log.d(TAG, "close: 资源释放");
    }

    public void releaseVideo() {
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

    public void releaseAudio() {
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

        Log.d(TAG, "close: 资源释放  音频");
    }

}

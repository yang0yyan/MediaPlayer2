//
// Created by Administrator on 2021/4/14.
//

#ifndef MEDIAPLAYER_VIDEO_CHANNEL_H
#define MEDIAPLAYER_VIDEO_CHANNEL_H
#include <jni.h>
#include <pthread.h>

#include <android/native_window.h>
#include <android/native_window_jni.h>

#include "SafeQueue.h"

extern "C"{
#include <libavutil/time.h>
#include <libavutil/mem.h>
};

class VideoChannel {

    class FrameData {
    public:
        uint8_t *data[4];
        int size[4];
        int64_t time;
    };

private:
    int isRun = 1;
    int toShow = 0;
    int videoWidth;
    int videoHeight;
    int videoFrameRate;
    int64_t frame_delay;
    int64_t startTime;
    ANativeWindow *native_window = nullptr;

    pthread_mutex_t mutex;
    pthread_t videoThread;

    SafeQueue<FrameData> frameQueue;

public:
    int init(JNIEnv *env,int width,int height,int frameRate);
    int setSurface(JNIEnv *env, jobject surface,int toShow);
    int play(int64_t startTime);
    int write(uint8_t *src[4], const int srcStride[4],int64_t relativeTime);
    int stop();
    int release();

    VideoChannel() {
        pthread_mutex_init(&mutex, NULL);
    }
    ~VideoChannel() {
        pthread_mutex_destroy(&mutex);
    }

private:
    static void *threadShow(void *arg);

    void releaseFrameData(FrameData frameData);
    int setSurData(uint8_t *src, int srcStride,VideoChannel *v);
    int setSurData2(uint8_t *src, int srcStride,VideoChannel *v);
    int setSurData3(uint8_t *src, int srcStride,VideoChannel *v);

    int show(VideoChannel *v);
    int stopShow();

    int64_t get_play_time(int64_t start_time);

    int createNativeWindow(JNIEnv *env, jobject surface);

    void releaseNativeWindow();
};


#endif //MEDIAPLAYER_VIDEO_CHANNEL_H

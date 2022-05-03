//
// Created by Administrator on 2022/1/10.
//

#ifndef VIDEOTESTAPP_VIDEO_CHANNEL_H
#define VIDEOTESTAPP_VIDEO_CHANNEL_H

#include <jni.h>
#include <malloc.h>
#include <cstring>
#include <pthread.h>
#include <android/native_window.h>
#include <android/native_window_jni.h>

extern "C"
{
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libswscale/swscale.h>
#include <libavutil/pixfmt.h>
#include <libavutil/opt.h>
#include <libavutil/imgutils.h>
};


class VideoChannel {


private:
    bool toShow = false;

    int videoWidth;
    int videoHeight;
    int rotation;

    pthread_mutex_t mutex;

    ANativeWindow *native_window = nullptr;

    int createNativeWindow(JNIEnv *env, jobject surface);

    void releaseNativeWindow();

public:
    VideoChannel() {
        pthread_mutex_init(&mutex, NULL);
    }

    ~VideoChannel() {
        pthread_mutex_destroy(&mutex);
    }

    int init(JNIEnv *env, int width, int height, int rotation);

    int setSurface(JNIEnv *env, jobject surface);

    int removeSurface();

    int write(uint8_t *src, int srcStride);

    void
    yuv420sp_to_rgb32(uint8_t *yuvbuffer, uint8_t *dst_data[4], int dst_linesize[4], int width,
                      int height);

};


#endif //VIDEOTESTAPP_VIDEO_CHANNEL_H

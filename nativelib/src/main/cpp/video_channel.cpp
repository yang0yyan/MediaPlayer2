//
// Created by Administrator on 2022/1/10.
//

#include "video_channel.h"

int VideoChannel::init(JNIEnv *env, int width, int height, int rotation) {
    this->videoWidth = width;
    this->videoHeight = height;
    this->rotation = rotation;
    return 1;
}

int VideoChannel::setSurface(JNIEnv *env, jobject surface) {
    createNativeWindow(env, surface);
    toShow = true;
    return 0;
}

int VideoChannel::removeSurface() {
    toShow = false;
    releaseNativeWindow();
    return 0;
}

int VideoChannel::createNativeWindow(JNIEnv *env, jobject surface) {
    releaseNativeWindow();
    pthread_mutex_lock(&mutex);
    native_window = ANativeWindow_fromSurface(env, surface);
    ANativeWindow_setBuffersGeometry(native_window, videoWidth, videoHeight,
                                     WINDOW_FORMAT_RGBA_8888);
    pthread_mutex_unlock(&mutex);
    return 1;
}


void VideoChannel::releaseNativeWindow() {
    pthread_mutex_lock(&mutex);
    if (native_window) {
        ANativeWindow_release(native_window);
        native_window = nullptr;
    }
    pthread_mutex_unlock(&mutex);
}

int VideoChannel::write(uint8_t *src, int srcLen) {
    int aaa = 0XFF & src[0];
    int bbb = 0XFF & src[1];
    int ccc = 0XFF & src[2];
    int ddd = 0XFF & src[3];

    if (!native_window || !toShow)return 0;

    uint8_t *dst_data[4];
    int dst_linesize[4];
    yuv420sp_to_rgb32(src, dst_data, dst_linesize, videoWidth, videoHeight);

    uint8_t *buffer = dst_data[0];
    int bufferStride = dst_linesize[0];

//    uint8_t *buffer = new uint8_t [videoHeight*videoWidth*4];
//    int bufferStride = videoWidth*4;
//
//    NV12_or_NV21_to_rgb32(12,src,buffer,videoWidth,videoHeight);

    if (!native_window || !toShow)return 0;
    ANativeWindow_Buffer windowBuffer;
    int code = ANativeWindow_lock(native_window, &windowBuffer, nullptr);
    if (code) {
        delete src;
        delete[] buffer;
        return -19;
    }
    auto *dst = static_cast<uint8_t *>(windowBuffer.bits);
    int dstStride = windowBuffer.stride * 4;
    if (!dst || dstStride <= 0)return 0;
    for (int h = 0; h < videoHeight; h++) {
        if (nullptr == src || dstStride <= 0 || !toShow) {
            break;
        }
        memcpy(dst + h * dstStride, buffer + h * bufferStride, (size_t) bufferStride);
    }
//    if (nullptr != src) {
//        memcpy(dst, buffer, (size_t) videoHeight * bufferStride);
//    }
    if (native_window && toShow) {
        ANativeWindow_unlockAndPost(native_window);
    }
    delete src;
    delete[] buffer;
    return 1;
}

void VideoChannel::yuv420sp_to_rgb32(uint8_t *yuvbuffer, uint8_t *rgb_data[4], int rgb_linesize[4],
                                     int width, int height) {
    //指针数组 , 数组中存放的是指针
    uint8_t *yuv_data[4];

    //普通的 int 数组
    int yuv_linesize[4];

    av_image_fill_arrays(yuv_data, yuv_linesize, yuvbuffer, AV_PIX_FMT_NV12, width, height, 1);

    //图像数据存储内存
    av_image_alloc(
            rgb_data,//指向图像数据的指针
            rgb_linesize,//图像数据存储的数据行数
            width,//图像的宽度
            height,//图像的高度
            AV_PIX_FMT_RGBA,//图像的像素格式,ARGB
            1);//未知

    struct SwsContext *sws_ctx = sws_getContext(
            width,//源图像的宽度
            width,//源图像的高度
            AV_PIX_FMT_NV12,//源图像的像素格式
            width,//目标图像的宽度
            width,//目标图像的高度
            AV_PIX_FMT_RGBA,//目标图像的像素格式
            SWS_BILINEAR,//使用的转换算法    有：快速、高质量
            nullptr,//源图像滤镜
            nullptr, //目标图像滤镜
            nullptr);//额外参数

    // NV12转RGBA
    sws_scale(sws_ctx, yuv_data,
              yuv_linesize, 0, height,
              rgb_data, rgb_linesize);
}

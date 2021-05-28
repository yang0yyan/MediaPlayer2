//
// Created by Administrator on 2021/4/14.
//

#include "video_channel.h"

int VideoChannel::init(JNIEnv *env, int width, int height, int frameRate) {
    this->videoWidth = width;
    this->videoHeight = height;
    this->videoFrameRate = frameRate;
    frame_delay = 1.0 / videoFrameRate / 2 * 1000 * 1000;
    return 1;
}

int VideoChannel::setSurface(JNIEnv *env, jobject surface, int toShow_) {
    toShow = toShow_;
    if (toShow_) {
        if (videoWidth && videoHeight) {
            createNativeWindow(env, surface);
        }
    } else {
        releaseNativeWindow();
    }

    return 1;
}

int VideoChannel::createNativeWindow(JNIEnv *env, jobject surface) {
    if (!surface)return 0;
    releaseNativeWindow();
    pthread_mutex_lock(&mutex);
    native_window = ANativeWindow_fromSurface(env, surface);
    ANativeWindow_setBuffersGeometry(native_window, videoWidth, videoHeight,
                                     WINDOW_FORMAT_RGBA_8888);
    pthread_mutex_unlock(&mutex);
    return 1;
}

int VideoChannel::play(int64_t startTime) {
    isRun = 1;
    this->startTime = startTime;
    frameQueue.setWork(1);
    pthread_create(&videoThread, nullptr, threadShow, this);
    return 1;
}

int VideoChannel::write(uint8_t *src[4], const int srcStride[4], int64_t relativeTime) {
    while (frameQueue.size() > 60) {
        if(!isRun)break;
        av_usleep(10 * 1000);
    }
    FrameData frameData{};
    for (int i = 0; i < 4; i++) {
        frameData.data[i] = src[i];
        frameData.size[i] = srcStride[i];
    }
    frameData.time = relativeTime;
    if (isRun)
        frameQueue.push(frameData);
    else
        releaseFrameData(frameData);
    return 1;
}

int VideoChannel::stop() {
    isRun = 0;
    frameQueue.setWork(0);
    return 1;
}

int VideoChannel::release() {
    if (videoThread)
        pthread_join(videoThread, 0);

    releaseNativeWindow();
    FrameData frameData{};
    while (!frameQueue.empty()) {
        frameQueue.pop(frameData);
        releaseFrameData(frameData);
    }
    return 1;
}

void VideoChannel::releaseNativeWindow() {
    pthread_mutex_lock(&mutex);
    if (native_window) {
        ANativeWindow_release(native_window);
        native_window = nullptr;
    }
    pthread_mutex_unlock(&mutex);
};

void *VideoChannel::threadShow(void *arg) {
    auto *v = static_cast<VideoChannel *>(arg);

    FrameData frameData{};
    while (v->isRun) {
        if(!v->frameQueue.pop(frameData)){
            av_usleep(10 * 1000);
            continue;
        }
        pthread_mutex_lock(&v->mutex);
        uint8_t *src = frameData.data[0];
        int srcStride = frameData.size[0];
        int64_t showTime = frameData.time;

//        int code = v->setSurData(src, srcStride, v);
//        int64_t delay = showTime - v->get_play_time(v->startTime);
//        if (delay > 0) {
//            av_usleep(delay);
//        }
//        if (code == -19)continue;
//        v->show(v);
//        code = v->setSurData2(src, srcStride, v);
//        int64_t delay2 = showTime + v->frame_delay - v->get_play_time(v->startTime);
//        if (delay2 > 0) {
//            av_usleep(delay2);
//        }
//        if (code == -19)continue;
//        v->show(v);
        //正常播放
        int code = v->setSurData3(src, srcStride, v);
        int64_t delay = showTime - v->get_play_time(v->startTime);
        if (delay > 0) {
            av_usleep(delay);
        }
        if (code == -19)continue;
        v->show(v);

        av_freep(frameData.data);
        pthread_mutex_unlock(&v->mutex);
    }
    return nullptr;
}

int VideoChannel::setSurData(uint8_t *src, int srcStride, VideoChannel *v) {
    if (!v->native_window || !v->toShow)return 0;
    int halfLen = srcStride / 2;
    int videoHeight = v->videoHeight;
    ANativeWindow_Buffer windowBuffer;
    int code = ANativeWindow_lock(native_window, &windowBuffer, nullptr);
    if (code)
        return -19;
    auto *dst = static_cast<uint8_t *>(windowBuffer.bits);
    int dstStride = windowBuffer.stride * 4;
    if (!dst)return 0;
    int h;
    for (h = 0; h < videoHeight; h++) {
        if (nullptr == src || dstStride <= 0) {
            break;
        }
        memcpy(dst + h * dstStride, src + h * srcStride, (size_t) halfLen);
    }
    return 1;
}

int VideoChannel::setSurData2(uint8_t *src, int srcStride, VideoChannel *v) {
    if (!v->native_window || !v->toShow)return 0;
    int halfLen = srcStride / 2;
    int videoHeight = v->videoHeight;
    ANativeWindow_Buffer windowBuffer;
    int code = ANativeWindow_lock(native_window, &windowBuffer, nullptr);
    if (code)
        return -19;
    auto *dst = static_cast<uint8_t *>(windowBuffer.bits);
    int dstStride = windowBuffer.stride * 4;
    if (!dst || dstStride <= 0)return 0;
    int h;
    for (h = 0; h < videoHeight; h++) {
        if (nullptr == src) {
            break;
        }
        memcpy(dst + h * dstStride, src + h * srcStride + halfLen, (size_t) halfLen);
    }
    return 1;
}

int VideoChannel::setSurData3(uint8_t *src, int srcStride, VideoChannel *v) {
    if (!v->native_window || !v->toShow)return 0;
    int videoHeight = v->videoHeight;
    ANativeWindow_Buffer windowBuffer;
    int code = ANativeWindow_lock(native_window, &windowBuffer, nullptr);
    if (code)
        return -19;
    auto *dst = static_cast<uint8_t *>(windowBuffer.bits);
    int dstStride = windowBuffer.stride * 4;
    if (!dst || dstStride <= 0)return 0;
    int h;
    for (h = 0; h < videoHeight; h++) {
        if (nullptr == src) {
            break;
        }
        memcpy(dst + h * dstStride, src + h * srcStride, (size_t) srcStride);
    }
    return 1;
}

int VideoChannel::show(VideoChannel *v) {
    if(v->native_window&&v->toShow){
        ANativeWindow_unlockAndPost(v->native_window);
    }
    return 0;
}

int64_t VideoChannel::get_play_time(int64_t start_time) {
    return (int64_t) (av_gettime() - start_time);
}

void VideoChannel::releaseFrameData(FrameData frameData) {
    if (frameData.data)
        av_freep(frameData.data);
}

int VideoChannel::stopShow() {
    toShow = 0;
    return 1;
}



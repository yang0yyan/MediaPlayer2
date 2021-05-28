//
// Created by Administrator on 2021/4/1.
//
#include "SafeQueue.h"
#include "video_channel.h"
#include "audio_channel.h"
#include "SafeQueue.h"
#include <pthread.h>

extern "C" {
#include <libavutil/time.h>
#include <libavformat/avformat.h>
#include <libavcodec/jni.h>
#include <libswscale/swscale.h>
#include <libavutil/imgutils.h>
#include <libswresample/swresample.h>
}

#ifndef MEDIAPLAYER_MEDIA_PLAYER_LIB_H
#define MEDIAPLAYER_MEDIA_PLAYER_LIB_H

#define MAX_AUDIO_FRAME_SIZE 48000 * 4

class MediaPlayer {

public:
    int init(JNIEnv *env, const char *url, jobject activity);
    int setSurface(JNIEnv *env, jobject surface,int toShow_);

    int play();

    void release();

private:
    AVFormatContext *formatContext = nullptr;
    AVCodecContext *video_codec_context = nullptr;
    AVCodecContext *audio_codec_context = nullptr;
    SwrContext *audioSwrContext;

    VideoChannel videoChannel;
    AudioChannel audioChannel;



    int video_stream_index = -1;
    int audio_stream_index = -1;
    int video_width = -1;
    int video_height = -1;
    AVRational video_time_base;
    AVRational audio_time_base;

    int64_t start_time;

    pthread_t write_thread;
    pthread_t video_thread;
    pthread_t audio_thread;
    pthread_t stop__thread;

    SafeQueue<AVPacket *> avPackets_v;
    SafeQueue<AVPacket *> avPackets_a;
    bool isPlaying = 0;


    int initFFmpeg(const char *url);

    void initData();

    int getAVDecoder();

    int video_player_prepare(JNIEnv *env, jobject activity);

    int audio_player_prepare();

    static void *write_packet_to_queue(void *arg);

    static void *decode_func_v(void *arg);
    static void *decode_func_a(void *arg);

    static int decode_video(MediaPlayer *player, AVPacket *packet);
    static int decode_audio(MediaPlayer *player, AVPacket *packet);
    int64_t get_play_time(int64_t start_time);
    void stop();
    void release_();


};

#endif //MEDIAPLAYER_MEDIA_PLAYER_LIB_H

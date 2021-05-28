//
// Created by Administrator on 2021/4/1.
//
#include "media-player-lib.h"

int MediaPlayer::init(JNIEnv *env, const char *url, jobject activity) {
    initData();
    int code = initFFmpeg(url);
    if (code)
        code = getAVDecoder();
    if (code)
        code = video_player_prepare(env, activity);
    if (code)
        code = audio_player_prepare();
    if (!code)
        initData();
    return code;
}

int MediaPlayer::setSurface(JNIEnv *env, jobject surface,int toShow_) {
    int code = videoChannel.setSurface(env, surface,toShow_);
    return code;
}

int MediaPlayer::play() {
    isPlaying = 1;
    start_time = av_gettime();
    videoChannel.play(start_time);
    audioChannel.play(start_time);
    int code = pthread_create(&write_thread, nullptr, write_packet_to_queue, this);
    if (!code)
        code = pthread_create(&video_thread, nullptr, decode_func_v, this);
    if (!code)
        code = pthread_create(&audio_thread, nullptr, decode_func_a, this);

    if (code) {
        initData();
        return 0;
    }
    return 1;
}

void MediaPlayer::release() {
    stop();
    audioChannel.stop();
    videoChannel.stop();
    pthread_join(write_thread, 0);
    pthread_join(audio_thread, 0);
    pthread_join(video_thread, 0);
    release_();
    audioChannel.release();
    videoChannel.release();
}

int MediaPlayer::initFFmpeg(const char *url) {
    //网络初始化
    avformat_network_init();
    //打开媒体地址  编解码器未打开。必须使用avformat_close_input()关闭流
    if (avformat_open_input(&formatContext, url, nullptr, nullptr) != 0) {
        //打开媒体失败
        return 0;
    }
    //获取音视频信息
    if (avformat_find_stream_info(formatContext, nullptr) < 0) {
        //查找媒体流失败
        return 0;
    }
    //音视频流数量
    unsigned int countTrack = formatContext->nb_streams;
    for (int i = 0; i < countTrack; i++) {//获取音视频流索引
        if (formatContext->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO
            && video_stream_index < 0) {
            video_stream_index = i;
            video_time_base = formatContext->streams[video_stream_index]->time_base;
        } else if (formatContext->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_AUDIO
                   && audio_stream_index < 0) {
            audio_stream_index = i;
            audio_time_base = formatContext->streams[audio_stream_index]->time_base;
        }
    }
    if (audio_stream_index == -1 && video_stream_index == -1) {
        //未获得音视频流索引
        return 0;
    }
    return 1;
}

int MediaPlayer::getAVDecoder() {
    //视频
    if (video_stream_index != -1) {
        AVCodecParameters *parameters_v = formatContext->streams[video_stream_index]->codecpar;
        //获取解码器
        AVCodec *video_codec = avcodec_find_decoder(parameters_v->codec_id);
        if (video_codec == nullptr)
            return 0;
        video_codec_context = avcodec_alloc_context3(video_codec);
        //设置解码器上下文
        if (avcodec_parameters_to_context(video_codec_context, parameters_v) < 0) {
            return 0;
        }
        //启动解码器
        if (avcodec_open2(video_codec_context, video_codec, nullptr) < 0) {
            return 0;
        }
    }
    //音频
    if (audio_stream_index != -1) {
        AVCodecParameters *parameters_a = formatContext->streams[audio_stream_index]->codecpar;
        AVCodec *audio_codec = avcodec_find_decoder(parameters_a->codec_id);
        if (audio_codec == nullptr)
            return 0;
        audio_codec_context = avcodec_alloc_context3(audio_codec);
        if (avcodec_parameters_to_context(audio_codec_context, parameters_a) < 0) {
            return 0;
        }
        if (avcodec_open2(audio_codec_context, audio_codec, nullptr) < 0) {
            return 0;
        }
    }

    return 1;
}

int MediaPlayer::video_player_prepare(JNIEnv *env, jobject activity) {
    video_width = video_codec_context->width;
    video_height = video_codec_context->height;
    AVRational frame_rate = formatContext->streams[video_stream_index]->avg_frame_rate;
    jclass player_class = env->FindClass(
            "com/yy/mediaplayer/activity/VideoPlayActivity");
    if (!player_class) {
        return 0;
    }
    jmethodID method = env->GetMethodID(player_class, "setSurfaceViewLayoutParams",
                                        "(II)V");
    env->CallVoidMethod(activity, method, video_width, video_height);

    int code = videoChannel.init(env,video_width, video_height, frame_rate.num / frame_rate.den);
    return code;
}

int MediaPlayer::audio_player_prepare() {
    int in_channelConfig = audio_codec_context->channel_layout;
    enum AVSampleFormat in_audioFormat = audio_codec_context->sample_fmt;
    int in_sampleRateInHz = audio_codec_context->sample_rate;

    int channelConfig = AV_CH_LAYOUT_STEREO;
    enum AVSampleFormat audioFormat = AV_SAMPLE_FMT_S16;
    int sampleRateInHz = in_sampleRateInHz;

    audioSwrContext = swr_alloc_set_opts(nullptr,
                                         channelConfig, audioFormat, sampleRateInHz,
                                         in_channelConfig, in_audioFormat, in_sampleRateInHz,
                                         0, nullptr);
    swr_init(audioSwrContext);
    int out_channel_nb = av_get_channel_layout_nb_channels(channelConfig);
    int code = audioChannel.init(sampleRateInHz, out_channel_nb, audioFormat);
    return code;
}

/**
 * ---------------------------------------------------------------------------------
 */

void *MediaPlayer::write_packet_to_queue(void *arg) {
    //Use auto when initializing with a cast to avoid duplicating the type name
    auto *m = static_cast<MediaPlayer *>(arg);
    m->avPackets_v.setWork(1);
    m->avPackets_a.setWork(1);
    int ret;
    int a = 0;
    int b = 0;
    while (m->isPlaying) {
        if(m->avPackets_v.size() > 200 || m->avPackets_a.size() > 200){
            av_usleep(10 * 1000);
            continue;
        }
        AVPacket *pkt = av_packet_alloc();
        if (!pkt) {
            av_usleep(10 * 1000);
            continue;
        }
        ret = av_read_frame(m->formatContext, pkt);
        if (ret != 0) {
            if (ret == AVERROR_EOF) {
                if (m->avPackets_v.empty() && m->avPackets_a.empty()) {
                    break;
                }
            } else {
                break;
            }
        } else {
            if (pkt->stream_index == m->video_stream_index) {
                ++a;
                m->avPackets_v.push(pkt);
//                av_packet_free(&pkt);
            } else if (pkt->stream_index == m->audio_stream_index) {
                ++b;
                m->avPackets_a.push(pkt);
//                av_packet_free(&pkt);
            } else { av_packet_free(&pkt); }
        }
    }
    return 0;
}

void *MediaPlayer::decode_func_v(void *arg) {
    auto *m = static_cast<MediaPlayer *>(arg);
    AVPacket *avPacket = nullptr;
    int ret = 0;
    while (m->isPlaying){
        int result_pop = m->avPackets_v.pop(avPacket);
        if (!result_pop) {
            continue;
        }
        ret = decode_video(m, avPacket);
        if (ret < 0) {
            if (m->avPackets_v.empty()) {
                free(&m->avPackets_v);
                break;
            }
        }
    }
    return nullptr;
}

void *MediaPlayer::decode_func_a(void *arg) {
    auto *m = static_cast<MediaPlayer *>(arg);
    AVPacket *avPacket = nullptr;
    int ret = 0;
    while (m->isPlaying){
        int result_pop = m->avPackets_a.pop(avPacket);
        if (!result_pop) {
            continue;
        }
        ret = decode_audio(m, avPacket);
        if (ret < 0) {
            if (m->avPackets_a.empty()) {
                free(&m->avPackets_a);
                break;
            }
        }
    }
    return nullptr;
}

int MediaPlayer::decode_video(MediaPlayer *player, AVPacket *packet) {

    //指针数组 , 数组中存放的是指针
    uint8_t *dst_data[4];

    //普通的 int 数组
    int dst_linesize[4];

    AVFrame *yuv_frame = av_frame_alloc();

    //图像数据存储内存
    av_image_alloc(
            dst_data,//指向图像数据的指针
            dst_linesize,//图像数据存储的数据行数
            player->video_width,//图像的宽度
            player->video_height,//图像的高度
            AV_PIX_FMT_ARGB,//图像的像素格式,ARGB
            1);//未知

    struct SwsContext *sws_ctx = sws_getContext(
            player->video_width,//源图像的宽度
            player->video_height,//源图像的高度
            player->video_codec_context->pix_fmt,//源图像的像素格式
            player->video_width,//目标图像的宽度
            player->video_height,//目标图像的高度
            AV_PIX_FMT_RGBA,//目标图像的像素格式
            SWS_BILINEAR,//使用的转换算法    有：快速、高质量
            nullptr,//源图像滤镜
            nullptr, //目标图像滤镜
            nullptr);//额外参数

    int frameFinished = 0;

    int send_packet_result = avcodec_send_packet(player->video_codec_context, packet);//0 on success
    switch (send_packet_result) {
        case 0:
        case AVERROR(EAGAIN):
            frameFinished = 1;
            break;
        case AVERROR_EOF:   //End of file 播放完毕
            return -1;
        case AVERROR(EINVAL):
        case AVERROR(ENOMEM):
        default:
            frameFinished = 0;
            break;
    }
    av_packet_free(&packet);
    if (!sws_ctx || send_packet_result != 0 || frameFinished == 0) {
        av_freep(dst_data);
        sws_freeContext(sws_ctx);
        return false;
    }
    //解码器接收并解码AVPacket数据到AVFrame中
    int receive_frame_result = avcodec_receive_frame(player->video_codec_context, yuv_frame);
    switch (receive_frame_result) {
        case 0:
            frameFinished = 1;
            break;
        case AVERROR(EAGAIN):
        case AVERROR_EOF:
        case AVERROR(EINVAL):
        case AVERROR_INPUT_CHANGED:
        default:
            frameFinished = 0;
            break;
    }
    if (frameFinished) {
        sws_scale(sws_ctx, (uint8_t const *const *) yuv_frame->data,
                  yuv_frame->linesize, 0, player->video_height,
                  dst_data, dst_linesize);
        int64_t video_pts_second =
                yuv_frame->best_effort_timestamp * av_q2d(player->video_time_base) * 1000 * 1000;
        player->videoChannel.write(dst_data, dst_linesize, video_pts_second);
    }
//
    sws_freeContext(sws_ctx);
    av_frame_free(&yuv_frame);
    return 0;
}

int MediaPlayer::decode_audio(MediaPlayer *player, AVPacket *packet) {
    int got_frame = 0;


    AVFrame *audio_frame = av_frame_alloc();


    int send_packet_result = avcodec_send_packet(player->audio_codec_context, packet);//0 on success
    switch (send_packet_result) {
        case 0:
        case AVERROR(EAGAIN):
            got_frame = 1;
            break;
        case AVERROR_EOF:   //End of file 播放完毕
            return -1;
        case AVERROR(EINVAL):
        case AVERROR(ENOMEM):
        default:
            got_frame = 0;
            break;
    }
    int64_t pts = packet->pts;
    av_packet_free(&packet);
    if (send_packet_result != 0 || got_frame == 0) {
        av_frame_free(&audio_frame);
        return false;
    }
    int receive_frame_result = avcodec_receive_frame(player->audio_codec_context, audio_frame);
    switch (receive_frame_result) {
        case 0:
            got_frame = 1;
            break;
        case AVERROR(EAGAIN):
        case AVERROR_EOF:
        case AVERROR(EINVAL):
        case AVERROR_INPUT_CHANGED:
        default:
            got_frame = 0;
            break;
    }
    uint8_t *audioBufferData = (uint8_t *) av_malloc(MAX_AUDIO_FRAME_SIZE);

    int samples_per_channel_count = swr_convert(
            player->audioSwrContext,
            &audioBufferData,
            MAX_AUDIO_FRAME_SIZE,
            (const uint8_t **) audio_frame->data,
            audio_frame->nb_samples);

    double audio_pts_second = audio_frame->pts * av_q2d(player->audio_time_base);

    int pcm_data_bit_size = samples_per_channel_count * 2 * 2;
    player->audioChannel.write(audioBufferData, pcm_data_bit_size, audio_pts_second);
    av_frame_free(&audio_frame);
    return 0;
}

int64_t MediaPlayer::get_play_time(int64_t start_time) {
    return (int64_t) (av_gettime() - start_time);
}

void MediaPlayer::initData() {
    release();
    audio_stream_index = -1;
    video_stream_index = -1;
}

void MediaPlayer::stop() {
    isPlaying = 0;
}

void MediaPlayer::release_() {
    AVPacket *avPacket;
    while (!avPackets_v.empty()) {
        avPackets_v.pop(avPacket);
        av_packet_free(&avPacket);
    }
    while (!avPackets_a.empty()) {
        avPackets_a.pop(avPacket);
        av_packet_free(&avPacket);
    }

    if (video_codec_context) {
        avcodec_close(video_codec_context);
        avcodec_free_context(&video_codec_context);
        video_codec_context = nullptr;
    }
    if (audio_codec_context) {
        avcodec_close(audio_codec_context);
        avcodec_free_context(&audio_codec_context);
        audio_codec_context = nullptr;
    }
    if (audioSwrContext) {
        swr_free(&audioSwrContext);
        audioSwrContext = nullptr;
    }
    if (formatContext) {
        avformat_close_input(&formatContext);//关闭输入流
        avformat_free_context(formatContext);
        formatContext = nullptr;
    }
}



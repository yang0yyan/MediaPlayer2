//
// Created by Administrator on 2021/4/14.
//

#ifndef MEDIAPLAYER_AUDIO_CHANNEL_H
#define MEDIAPLAYER_AUDIO_CHANNEL_H

#include <pthread.h>
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>

#include "SafeQueue.h"

extern "C"{
#include <libavutil/time.h>
#include <libavutil/mem.h>
}

class AudioChannel {
    class FrameData {
    public:
        uint8_t *data;
        int size;
        int64_t time;
    };
private:
    int64_t startTime;
    int64_t nowTime;
    int sampleRateInHz = 44100;
    int channelConfig = SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT;
    int audioFormat = SL_PCMSAMPLEFORMAT_FIXED_16;
    int isRun=0;
    int threadRun = 0;

    pthread_t audioThread;

    SafeQueue<FrameData> frameQueue;

    SLresult result;
    SLObjectItf engineObject;//引擎对象
    SLEngineItf engineEngine;//引擎接口
    SLObjectItf outputMixObject = nullptr;//输出混音器对象

    SLEnvironmentalReverbItf outputMixEnvironmentalReverb = nullptr;//环境混响接口
    SLEnvironmentalReverbSettings reverbSettings = SL_I3DL2_ENVIRONMENT_PRESET_STONECORRIDOR;

    SLObjectItf bqPlayerObject = nullptr;//播放器对象
    SLmilliHertz bqPlayerSampleRate = 0;

    SLPlayItf bqPlayerPlay;//播放器接口     开始 暂停 停止 播放
    SLAndroidSimpleBufferQueueItf bqPlayerBufferQueue;//播放器缓冲队列接口    用于控制 音频 缓冲区数据 播放

    SLEffectSendItf bqPlayerEffectSend;//效果器发送接口
    SLMuteSoloItf bqPlayerMuteSolo;;//效果器发送接口
    SLVolumeItf bqPlayerVolume;//音量控制接口

public:
    int init(int sampleRateInHz, int channelConfig, int audioFormat);

    int play(int64_t startTime);

    int write(uint8_t *data, int size, int64_t time);

    int stop();

    int release();

private:

    static void *threadPlay(void *arg);
    int64_t get_play_time(int64_t start_time);

    void releaseFrameData(FrameData frameData);


    int createAudio();

    int CreateEngine();

    int createOutputMix();

    int setOutputMix();

    int create();

    int getOther1();

    int getOther2();

    static void bqPlayerCallback(SLAndroidSimpleBufferQueueItf bq, void *context);


};


#endif //MEDIAPLAYER_AUDIO_CHANNEL_H

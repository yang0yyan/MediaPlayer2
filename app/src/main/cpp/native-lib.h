//
// Created by lenovo on 2020/10/14.
//

#include <jni.h>
#include <malloc.h>
#include <cstring>
#include "media-player-lib.h"

#ifndef MY_APPLICATION_NATIVE_LIB_H
#define MY_APPLICATION_NATIVE_LIB_H

extern "C" {
#include <libavcodec/jni.h>
};


MediaPlayer mediaPlayer;


JavaVM *javaVM;
#endif //MY_APPLICATION_NATIVE_LIB_H

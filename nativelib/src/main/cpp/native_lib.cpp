//
// Created by Administrator on 2022/1/10.
//

#include "native_lib.h"

jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    return JNI_VERSION_1_6;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_nativelib_IMediaPlayer_readFile(JNIEnv *env, jclass clazz, jstring file_path,
                                                 jobject activity) {
    const char *file_name = env->GetStringUTFChars(file_path, JNI_FALSE);
    int code = mediaPlayer.readFile(env, file_name, activity);
    return code;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_nativelib_IMediaPlayer_release(JNIEnv *env, jclass clazz) {
    mediaPlayer.release();
}

///-------------------------------视频相关-----------------------------------

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_nativelib_IMediaPlayer_initVideo(JNIEnv *env, jclass clazz, jint width,
                                                jint height,jint rotation) {
    mediaPlayer.initVideo(env, width, height,rotation);
    return 1;
}

extern "C"
JNIEXPORT jint

JNICALL
Java_com_example_nativelib_IMediaPlayer_setSurface(JNIEnv *env, jclass clazz, jobject surface) {
    mediaPlayer.setSurface(env, surface);
    return 1;
}

extern "C"
JNIEXPORT jint

JNICALL
Java_com_example_nativelib_IMediaPlayer_removeSurface(JNIEnv *env, jclass clazz) {
    mediaPlayer.removeSurface();
    return 1;
}

extern "C"
JNIEXPORT jint

JNICALL
Java_com_example_nativelib_IMediaPlayer_videoWrite(JNIEnv *env, jclass clazz, jbyteArray bytes) {
    uint8_t *audio_buffer;
    jsize size = env->GetArrayLength(bytes);
    jbyte *sample_byte_array = env->GetByteArrayElements(bytes, JNI_FALSE);

//    audio_buffer = new uint8_t[size];
//    memcpy(sample_byte_array, audio_buffer, (size_t) size);

    char *chars =  new char[size];
    memcpy(chars, sample_byte_array, size);

    mediaPlayer.videoWrite(reinterpret_cast<uint8_t *>(chars), size);
    env->ReleaseByteArrayElements(bytes, sample_byte_array, JNI_FALSE);
    return 1;
}

///-----------------------------------------------------------------------------------




#include <libavcodec/jni.h>
#include "native-lib.h"

jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    return JNI_VERSION_1_6;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_yy_mediaplayer_model_IMediaPlayer_setup(JNIEnv *env, jclass clazz,
                                               jstring file_path, jobject activity) {
    // TODO: implement setup()
    const char *file_name = env->GetStringUTFChars(file_path, JNI_FALSE);
    int code = mediaPlayer.init(env, file_name, activity);
    return code;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_yy_mediaplayer_model_IMediaPlayer_setSurface(JNIEnv *env, jclass clazz,jobject surface,jint toShow) {
    // TODO: implement play()
    int code = mediaPlayer.setSurface(env,surface,toShow);
    return code;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_yy_mediaplayer_model_IMediaPlayer_play(JNIEnv *env, jclass clazz) {
    // TODO: implement play()
    int code = mediaPlayer.play();
    return code;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_yy_mediaplayer_model_IMediaPlayer_release(JNIEnv *env, jclass clazz) {
    //surface->release();
    mediaPlayer.release();
}



extern "C"
JNIEXPORT jint JNICALL
Java_com_yy_mediaplayer_model_IMediaPlayer_init(JNIEnv *env, jclass clazz, jobject surface_,
                                              jint video_width, jint video_height) {
    // TODO: implement setup()
    //surface->init(env,surface_,video_width,video_height);
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_yy_mediaplayer_model_IMediaPlayer_write(JNIEnv *env, jclass clazz, jbyteArray byteArray) {
    uint8_t *audio_buffer;
    jsize size = env->GetArrayLength(byteArray);
    jbyte *sample_byte_array = env->GetByteArrayElements(byteArray, nullptr);
    audio_buffer = static_cast<uint8_t *>(malloc(sizeof(sample_byte_array)));
    memcpy(sample_byte_array, audio_buffer, (size_t) size);
    //surface->show(audio_buffer);
    return 0;
}
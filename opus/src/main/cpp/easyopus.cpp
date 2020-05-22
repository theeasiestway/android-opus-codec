//
// Created by Loboda Alexey on 21.05.2020.
//

#include <string>
#include <jni.h>
#include "codec/CodecOpus.h"

CodecOpus codec;

//
// Encoding
//

extern "C"
JNIEXPORT jint JNICALL Java_com_theeasiestway_opus_Opus_encoderInit(JNIEnv *env, jobject thiz, jint sample_rate, jint num_channels, jint application) {
    return codec.encoderInit(sample_rate, num_channels, application);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_theeasiestway_opus_Opus_encoderSetBitrate(JNIEnv *env, jobject thiz, jint bitrate) {
    return codec.encoderSetBitrate(bitrate);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_theeasiestway_opus_Opus_encoderSetComplexity(JNIEnv *env, jobject thiz, jint complexity) {
    return codec.encoderSetComplexity(complexity);
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_theeasiestway_opus_Opus_encode___3BII(JNIEnv *env, jobject thiz, jbyteArray bytes, jint length, jint frame_size) {
    jbyte *nativeBytes = env->GetByteArrayElements(bytes, 0);
    std::vector<uint8_t> encodedData = codec.encode((uint8_t *) nativeBytes, length, frame_size);
    int encodedSize = encodedData.size();
    if (encodedSize <= 0) return nullptr;

    jbyteArray result = env->NewByteArray(encodedSize);
    env->SetByteArrayRegion(result, 0, encodedSize, (jbyte *) encodedData.data());
    env->ReleaseByteArrayElements(bytes, nativeBytes, 0);

    return result;
}

extern "C"
JNIEXPORT jshortArray JNICALL
Java_com_theeasiestway_opus_Opus_encode___3SII(JNIEnv *env, jobject thiz, jshortArray shorts, jint length, jint frame_size) {
    jshort *nativeShorts = env->GetShortArrayElements(shorts, 0);
    std::vector<short> encodedData = codec.encode(nativeShorts, length, frame_size);
    int encodedSize = encodedData.size();
    if (encodedSize <= 0) return nullptr;

    jshortArray result = env->NewShortArray(encodedSize);
    env->SetShortArrayRegion(result, 0, encodedSize, encodedData.data());
    env->ReleaseShortArrayElements(shorts, nativeShorts, 0);

    return result;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_theeasiestway_opus_Opus_encoderRelease(JNIEnv *env, jobject thiz) {
    codec.encoderRelease();
}

//
// Decoding
//

extern "C"
JNIEXPORT jint JNICALL
Java_com_theeasiestway_opus_Opus_decoderInit(JNIEnv *env, jobject thiz, jint sample_rate, jint num_channels) {
    return codec.decoderInit(sample_rate, num_channels);
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_theeasiestway_opus_Opus_decode___3BII(JNIEnv *env, jobject thiz, jbyteArray bytes, jint length, jint frame_size) {
    jbyte *nativeBytes = env->GetByteArrayElements(bytes, 0);
    std::vector<uint8_t> encodedData = codec.decode((uint8_t *) nativeBytes, length, frame_size);
    int encodedSize = encodedData.size();
    if (encodedSize <= 0) return nullptr;

    jbyteArray result = env->NewByteArray(encodedSize);
    env->SetByteArrayRegion(result, 0, encodedSize, (jbyte *) encodedData.data());
    env->ReleaseByteArrayElements(bytes, nativeBytes, 0);

    return result;
}

extern "C"
JNIEXPORT jshortArray JNICALL
Java_com_theeasiestway_opus_Opus_decode___3SII(JNIEnv *env, jobject thiz, jshortArray shorts, jint length, jint frame_size) {
    jshort *nativeShorts = env->GetShortArrayElements(shorts, 0);
    std::vector<short> encodedData = codec.decode(nativeShorts, length, frame_size);
    int encodedSize = encodedData.size();
    if (encodedSize <= 0) return nullptr;

    jshortArray result = env->NewShortArray(encodedSize);
    env->SetShortArrayRegion(result, 0, encodedSize, encodedData.data());
    env->ReleaseShortArrayElements(shorts, nativeShorts, 0);

    return result;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_theeasiestway_opus_Opus_decoderRelease(JNIEnv *env, jobject thiz) {
    codec.decoderRelease();
}
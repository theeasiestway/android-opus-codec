//
// Created by Loboda Alexey on 21.05.2020.
//

#include "CodecOpus.h"
#include "../utils/SamplesConverter.h"
#include "../utils/logger.h"

//
// Encoder
//

int CodecOpus::encoderInit(int sampleRate, int numChannels, int application) {

    if (numChannels != 1 && numChannels != 2) LOGE(TAG, "[encoderInit] numChannels is incorrect: %d - it must be either 1 or 2, otherwise the encoder may works incorrectly", numChannels);

    int size = opus_encoder_get_size(numChannels);

    if (size <= 0) {
        LOGE(TAG, "[encoderInit] couldn't init encoder with size: %d", size);
        return size;
    }

    encoder = (OpusEncoder*) malloc((size_t) size);

    int ret = opus_encoder_init(encoder, sampleRate, numChannels, application);

    if (ret) {
        LOGE(TAG, "[encoderInit] couldn't init encoder ret: %d; error: %s", ret, opus_strerror(ret));
        free(encoder);
        return -1;
    } else LOGD(TAG, "[encoderInit] encoder successfully initialized");

    return 0;
}

int CodecOpus::encoderSetBitrate(int bitrate) {

    int ret = checkForNull("encoderSetBitrate", true);
    if (ret < 0) return ret;

    return opus_encoder_ctl(encoder, OPUS_SET_BITRATE(bitrate));
}

int CodecOpus::encoderSetComplexity(int complexity) {
    int ret = checkForNull("encoderSetComplexity", true);
    if (ret < 0) return ret;

    return opus_encoder_ctl(encoder, OPUS_SET_COMPLEXITY(complexity));
}

int CodecOpus::encoderSetVBR(int vbr) {
    int ret = checkForNull("encoderSetVBR", true);
    if (ret < 0) return ret;

    return opus_encoder_ctl(encoder, OPUS_SET_VBR(vbr));
}

std::vector<short> CodecOpus::encode(short *shorts, int length, int frameSize) {
    std::vector<uint8_t> bytes = SamplesConverter::convert(&shorts, length);
    std::vector<uint8_t> encoded = encode(bytes.data(), frameSize);
    uint8_t *data = encoded.data();
    return SamplesConverter::convert(&data, encoded.size());
}

std::vector<uint8_t> CodecOpus::encode(uint8_t *bytes, int frameSize) {
    std::vector<uint8_t> result;

    int ret = checkForNull("encode", true);
    if (ret < 0) return result;

    int maxBytesCount = sizeof(unsigned char) * 1024;
    unsigned char *outBuffer = (unsigned char*) malloc((size_t) maxBytesCount);

    int resultLength = opus_encode(encoder, (opus_int16 *) bytes, frameSize, outBuffer, maxBytesCount);
    if (resultLength <= 0) {
        LOGE(TAG, "[encode] error: %s", opus_strerror(resultLength));
        return result;
    }

    std::copy(&outBuffer[0], &outBuffer[resultLength], std::back_inserter(result));
    free(outBuffer);
    return result;
}

void CodecOpus::encoderRelease() {
    if (encoder) opus_encoder_destroy(encoder);
    encoder = nullptr;
}

//
// Decoder
//

int CodecOpus::decoderInit(int sampleRate, int numChannels) {

    if (numChannels != 1 && numChannels != 2) LOGE(TAG, "[decoderInit] numChannels is incorrect: %d - it must be either 1 or 2, otherwise the decoder may works incorrectly", numChannels);

    int size = opus_decoder_get_size(numChannels);

    if (size <= 0) {
        LOGE(TAG, "[decoderInit] couldn't init decoder with size: %d", size);
        return size;
    }

    decoder = (OpusDecoder*) malloc((size_t) size);

    int ret = opus_decoder_init(decoder, sampleRate, numChannels);

    if (ret) {
        LOGE(TAG, "[decoderInit] couldn't init decoder ret: %d; error: %s", ret, opus_strerror(ret));
        free(decoder);
        return -1;
    } else LOGD(TAG, "[decoderInit] decoder successfully initialized");

    decoderNumChannels = numChannels;

    return 0;
}

std::vector<short> CodecOpus::decode(short *shorts, int length, int frameSize, int fec) {
    std::vector<uint8_t> bytes = SamplesConverter::convert(&shorts, length);
    std::vector<uint8_t> decoded = decode(bytes.data(), bytes.size(), frameSize, fec);
    uint8_t *data = decoded.data();
    return SamplesConverter::convert(&data, decoded.size());
}

std::vector<uint8_t> CodecOpus::decode(uint8_t *bytes, int length, int frameSize, int fec) {
    std::vector<uint8_t> result;

    int ret = checkForNull("decode", false);
    if (ret < 0) return result;

    opus_int16 *outBuffer = (opus_int16*) malloc(sizeof(opus_int16) * 1024);

    int resultLength = opus_decode(decoder, bytes, length, outBuffer, frameSize, fec);
    if (resultLength <= 0) {
        LOGE(TAG, "[decode] error: %s", opus_strerror(resultLength));
        return result;
    }

    result = SamplesConverter::convert(&outBuffer, resultLength * decoderNumChannels);

    free(outBuffer);
    return result;
}

void CodecOpus::decoderRelease() {
    decoderNumChannels = -1;
    if (decoder) opus_decoder_destroy(decoder);
    decoder = nullptr;
}

int CodecOpus::checkForNull(const char *methodName, bool isEncoder) {
    const char *typeName = isEncoder ? "encoder" : "decoder";

    if (isEncoder && !encoder || !isEncoder && !decoder) {
        LOGE(TAG, "[%s] %s wasn't initialized you must call %sInit() first", methodName, typeName, typeName);
        return -1;
    }
    return 0;
}

CodecOpus::~CodecOpus() {
    encoderRelease();
    decoderRelease();
}
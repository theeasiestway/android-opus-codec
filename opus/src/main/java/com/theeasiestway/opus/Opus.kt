package com.theeasiestway.opus

import android.util.Log


//
// Created by Loboda Alexey on 21.05.2020.
//

class Opus {

    companion object {

        val TAG = "Opus"

        init {
            try { System.loadLibrary("easyopus") }
            catch (e: Exception) { Log.e(TAG, "Couldn't load opus library: $e") }
        }
    }

    //
    // Encoder
    //

    fun encoderInit(sampleRate: Constants.SampleRate, channels: Constants.Channels, application: Constants.Application): Int {
        return encoderInit(sampleRate.v, channels.v, application.v)
    }
    private external fun encoderInit(sampleRate: Int, numChannels: Int, application: Int): Int

    fun encoderSetBitrate(bitrate: Constants.Bitrate): Int {
        return encoderSetBitrate(bitrate.v)
    }
    private external fun encoderSetBitrate(bitrate: Int): Int

    fun encoderSetComplexity(complexity: Constants.Complexity): Int {
        return encoderSetComplexity(complexity.v)
    }
    private external fun encoderSetComplexity(complexity: Int): Int

    external fun encode(bytes: ByteArray, length: Int, frameSize: Int): ByteArray?
    external fun encode(shorts: ShortArray, length: Int, frameSize: Int): ShortArray?
    external fun encoderRelease()

    //
    // Decoder
    //

    fun decoderInit(sampleRate: Constants.SampleRate, channels: Constants.Channels): Int {
        return decoderInit(sampleRate.v, channels.v)
    }
    private external fun decoderInit(sampleRate: Int, numChannels: Int): Int

    external fun decode(bytes: ByteArray, length: Int, frameSize: Int): ByteArray?
    external fun decode(shorts: ShortArray, length: Int, frameSize: Int): ShortArray?
    external fun decoderRelease()
}

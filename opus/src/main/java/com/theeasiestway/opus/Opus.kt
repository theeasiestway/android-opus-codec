package com.theeasiestway.opus

import android.util.Log

//
// Created by Loboda Alexey on 21.05.2020.
//

class Opus {

    companion object {

        val TAG = "Opus"

        init {
            try {
                System.loadLibrary("easyopus")
            } catch (e: Exception) {
                Log.e(TAG, "Couldn't load opus library: $e")
            }
        }
    }


    external fun encoderInit(sampleRate: Int, numChannels: Int, application: Int): Int
    external fun encoderSetBitrate(bitrate: Int): Int
    external fun encoderSetComplexity(complexity: Int): Int
    external fun encode(bytes: ByteArray, length: Int, frameSize: Int): ByteArray?
    external fun encode(shorts: ShortArray, length: Int, frameSize: Int): ShortArray?
    external fun encoderRelease()

    external fun decoderInit(sampleRate: Int, numChannels: Int): Int
    external fun decode(bytes: ByteArray, length: Int, frameSize: Int): ByteArray?
    external fun decode(shorts: ShortArray, length: Int, frameSize: Int): ShortArray?
    external fun decoderRelease()
}

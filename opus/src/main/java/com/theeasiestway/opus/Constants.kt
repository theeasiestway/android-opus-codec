package com.theeasiestway.opus

import androidx.annotation.IntRange

//
// Created by Loboda Alexey on 22.05.2020.
//

object Constants {

    class SampleRate private constructor(val v: Int) {
        companion object {
            fun _8000() = SampleRate(8000)
            fun _12000() = SampleRate(12000)
            fun _16000() = SampleRate(16000)
            fun _24000() = SampleRate(24000)
            fun _48000() = SampleRate(48000)
        }
    }

    class Channels private constructor(val v: Int) {
        companion object {
            fun mono() = Channels(1)
            fun stereo() = Channels(2)
        }
    }

    class Application private constructor(val v: Int) {

        /** voip - Best for most VoIP/videoconference applications where listening quality and intelligibility matter most.
         * audio - Best for broadcast/high-fidelity application where the decoded audio should be as close as possible to the input.
         * lowdelay - Only use when lowest-achievable latency is what matters most. Voice-optimized modes cannot be used. */

        companion object {
            fun voip() = Application(2048)
            fun audio() = Application(2049)
            fun lowdelay() = Application(2051)
        }
    }

    class Complexity private constructor(val v: Int) {

        /** The Opus encoder uses its maximum algorithmic complexity setting of 10 by default.
         * This means that it does not hesitate to use CPU to give you the best quality encoding at a given bitrate.
         * If the CPU usage is too high for the system you are using Opus on, you can try a lower complexity setting.
         * The allowed values span from 10 (highest CPU usage and quality) down to 0 (lowest CPU usage and quality). */

        companion object {
            fun instance(@IntRange(from = 0, to = 10) value: Int): Complexity {
                if (value < 0) return Complexity(0)
                if (value > 10) return Complexity(10)
                return Complexity(value)
            }
        }
    }

    class Bitrate private constructor(val v: Int) {

        /** Configures the bitrate in the encoder.
         * Rates from 500 to 512000 bits per second are meaningful, as well as the
         * special values #auto and #max.
         * The value #max can be used to cause the codec to use as much
         * rate as it can, which is useful for controlling the rate by adjusting the
         * output buffer size.
         **/

        companion object {
            fun instance(@IntRange(from = 500, to = 512000) value: Int): Bitrate {
                if (value < 500) return Bitrate(500)
                if (value > 512000) return Bitrate(512000)
                return Bitrate(value)
            }

            fun auto(): Bitrate {
                return Bitrate(-1000)
            }

            fun max(): Bitrate {
                return Bitrate(-1)
            }
        }
    }

    class FrameSize private constructor(val v: Int) {

        /**
         * Number of samples per channel in the input signal.
         * This must be an Opus frame size for
         * the encoder's sampling rate.
         * For example, at 48 kHz the permitted
         * values are 120, 240, 480, 960, 1920, and 2880.
         * Passing in a duration of less than 10 ms (480 samples at 48 kHz) will
         * prevent the encoder from using the LPC or hybrid modes.
         *
         *
         **/

        companion object {
            fun _120() = FrameSize(120)
            fun _160() = FrameSize(160)
            fun _240() = FrameSize(240)
            fun _320() = FrameSize(320)
            fun _480() = FrameSize(480)
            fun _640() = FrameSize(640)
            fun _960() = FrameSize(960)
            fun _1280() = FrameSize(1280)
            fun _1920() = FrameSize(1920)
            fun _2560() = FrameSize(2560)
            fun _2880() = FrameSize(2880)
            fun _custom(value: Int) = FrameSize(value)
        }
    }
}
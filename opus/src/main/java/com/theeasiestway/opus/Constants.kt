package com.theeasiestway.opus

import androidx.annotation.IntRange

//
// Created by Loboda Alexey on 22.05.2020.
//

object Constants {

    class SampleRate private constructor(val v: Int) {
        companion object {
            fun sr_8000() = SampleRate(8000)
            fun sr_12000() = SampleRate(12000)
            fun sr_16000() = SampleRate(16000)
            fun sr_24000() = SampleRate(24000)
            fun sr_48000() = SampleRate(48000)
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

        /** the encoder's computational complexity: 0 - the lowest complexity, 10 - the highest */

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
}
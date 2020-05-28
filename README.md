# android-opus-codec
An android wrapper around [libopus 1.3.1](https://opus-codec.org/release/stable/2019/04/12/libopus-1_3_1.html) written on C++ and Kotlin.

## Supported features
1. Encoding PCM audio into Opus.
2. Decoding Opus audio into PCM.
3. Sample rate of input audio from 8000Hz to 48000Hz.
4. Different frame sizes.
5. Mono or stereo input audio.
6. Input in bytes or shorts.
7. Output in bytes or shorts.
8. Convert from bytes to shorts and vice versa.
9. Bitrate setting.
10.Complexity setting.

## Supported ABIs
armeabi-v7a, arm64-v8a, x86, x86_64

## How to use

#### Init encoder and decoder
```kotlin
val SAMPLE_RATE = Constants.SampleRate._48000()       // samlpe rate of the input audio
val CHANNELS = Constants.Channels.stereo()            // type of the input audio mono or stereo 
val APPLICATION = Constants.Application.audio()       // coding mode of the encoder
var FRAME_SIZE = Constants.FrameSize._120()           // default frame size for 48000Hz

val codec = Opus()                                    // getting an instance of Codec
codec.encoderInit(SAMPLE_RATE, CHANNELS, APPLICATION) // init encoder
codec.decoderInit(SAMPLE_RATE, CHANNELS)              // init decoder
```

#### Setup the encoder
```kotlin
/* this step is optional because the encoder can use default values */
val COMPLEXITY = Constants.Complexity.instance(10)    // encoder's algorithmic complexity 
val BITRATE = Constants.Bitrate.max()                 // encoder's bitrate
codec.encoderSetComplexity(COMPLEXITY)                // set the complexity
codec.encoderSetBitrate(BITRATE)                      // set the bitrate
```

#### Encoding
```kotlin
val frame = ...                                       // gets a chunk of audio from some source as an array of bytes or shorts
val encoded = codec.encode(frame, FRAME_SIZE)         // encode a chunk of audio into Opus
if (encoded != null) Log.d("Opus", "encoded chunk size: ${encoded.size}")
```

#### Decoding
```kotlin
val decoded = codec.decode(encoded, FRAME_SIZE)       // decode a chunk of audio into PCM
if (decoded != null) Log.d("Opus", "decoded chunk size: ${decoded.size}")
```

#### Freeing resources when closing the app
```kotlin
codec.encoderRelease()
codec.decoderRelease()
```

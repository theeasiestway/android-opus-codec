package com.theeasiestway.opusapp.mic

import android.media.*
import android.media.audiofx.AutomaticGainControl
import android.media.audiofx.NoiseSuppressor
import android.util.Log

//
// Created by Loboda Alexey on 21.05.2020.
//

object ControllerAudio {

    private const val TAG = "ControllerAudio"
    private var frameSize: Int = -1
    private lateinit var recorder: AudioRecord
    private var micEnabled = false
    private lateinit var track: AudioTrack
    private var trackReady = false
    private var noiseSuppressor: NoiseSuppressor? = null
    private var automaticGainControl: AutomaticGainControl? = null

    //
    // Record
    //

    fun initRecorder(sampleRate: Int, frameSize: Int, isMono: Boolean) {
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, if (isMono) AudioFormat.CHANNEL_IN_MONO else AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT)
        for (i in 0..5) {
            try {
                recorder = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    sampleRate,
                    if (isMono) AudioFormat.CHANNEL_IN_MONO else AudioFormat.CHANNEL_IN_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize
                )

                ControllerAudio.frameSize = frameSize

                if (NoiseSuppressor.isAvailable()) {
                    try {
                        noiseSuppressor = NoiseSuppressor.create(recorder.audioSessionId)
                        if (noiseSuppressor != null) noiseSuppressor!!.enabled = true
                    } catch (e: Exception) { Log.e(TAG, "[initRecorder] unable to init noise suppressor: $e") }
                }

                if (AutomaticGainControl.isAvailable()) {
                    try {
                        automaticGainControl = AutomaticGainControl.create(recorder.audioSessionId)
                        if (automaticGainControl != null) automaticGainControl!!.enabled = true
                    } catch (e: Exception) { Log.e(TAG, "[initRecorder] unable to init automatic gain control: $e") }
                }
                onMicStateChange(true)
                break
            } catch (e: Exception) { Log.e(TAG, "[initRecorder] error: $e") }
        }
    }

    fun startRecord() {
        if (recorder.state == AudioRecord.STATE_INITIALIZED) {
            recorder.startRecording()
            micEnabled = true
        }
    }

    fun getFrame(): ByteArray? {
        val frame = ByteArray(frameSize)
        var offset = 0
        var remained = frame.size
        while (remained > 0) {
            val read = recorder.read(frame, offset, remained)
            offset += read
            remained -= read
        }
        if (remained <= 0) return frame
        return null
    }

    fun getFrameShort(): ShortArray? {
        val frame = ShortArray(frameSize)
        var offset = 0
        var remained = frame.size
        while (remained > 0) {
            val read = recorder.read(frame, offset, remained)
            offset += read
            remained -= read
        }
        if (remained <= 0) return frame
        return null
    }

    fun onMicStateChange(micEnabled: Boolean) {
        ControllerAudio.micEnabled = micEnabled
    }

    fun stopRecord() {
        try {
            if (recorder.state == AudioTrack.STATE_INITIALIZED) recorder.stop()
            recorder.release()
            noiseSuppressor?.release()
            automaticGainControl?.release()
        } catch (e: Exception) { Log.e(TAG, "[stopRecord] error: $e") }
    }

    //
    // Play
    //

    fun initTrack(sampleRate: Int, isMono: Boolean) {
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, if (isMono) AudioFormat.CHANNEL_IN_MONO else AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT)
        for (i in 0..5) {
            try {
                track = AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate,
                    if (isMono) AudioFormat.CHANNEL_OUT_MONO else AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize,
                    AudioTrack.MODE_STREAM
                )

               // track.setStereoVolume(0f, 1f) // it may be useful for stereo audio

                if (track.state == AudioRecord.STATE_INITIALIZED) {
                    track.play()
                    trackReady = true
                    break
                }
            } catch (e: Exception) { Log.e(TAG, "[initTrack] error: $e") }
        }
    }

    fun write(frame: ShortArray) {
        if (!trackReady) return
        track.write(frame, 0, frame.size)
    }

    fun write(frame: ByteArray) {
        if (!trackReady) return
        track.write(frame, 0, frame.size)
    }

    fun stopTrack() {
        if (!trackReady) return
        if (track.state == AudioTrack.STATE_INITIALIZED) track.stop()
        track.flush()
        trackReady = false
    }

    fun destroy() {
        stopRecord()
        stopTrack()
    }
}
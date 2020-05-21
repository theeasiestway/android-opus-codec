package com.theeasiestway.opusapp.mic

import android.media.*
import android.media.audiofx.AutomaticGainControl
import android.media.audiofx.NoiseSuppressor
import android.util.Log

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

    fun initRecorder(sampleRate: Int, frameSize: Int) {
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        for (i in 0..5) {
            try {
                recorder = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    sampleRate,
                    AudioFormat.CHANNEL_IN_MONO,
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
        val count = recorder.read(frame, 0, frameSize)
        if (count > 0) return frame
        return null
    }

    fun onMicStateChange(micEnabled: Boolean) {
        ControllerAudio.micEnabled = micEnabled
    }

    fun stopRecord() {
        try {
            if (recorder.state == AudioTrack.STATE_INITIALIZED) recorder.stop()
            noiseSuppressor?.release()
            automaticGainControl?.release()
        } catch (e: Exception) { Log.e(TAG, "[stopRecord] error: $e") }
    }

    //
    // Play
    //

    fun initTrack(sampleRate: Int) {
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        for (i in 0..5) {
            try {
                track = AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize,
                    AudioTrack.MODE_STREAM
                )
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
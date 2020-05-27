package com.theeasiestway.opusapp

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.theeasiestway.opus.Constants
import com.theeasiestway.opus.Opus
import com.theeasiestway.opusapp.mic.ControllerAudio

//
// Created by Loboda Alexey on 21.05.2020.
//

class MainActivity : AppCompatActivity() {

    private val TAG = "OpusActivity"
    private val audioPermission = android.Manifest.permission.RECORD_AUDIO
    private val readPermission = android.Manifest.permission.READ_EXTERNAL_STORAGE
    private val writePermission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE

    private lateinit var vPlay: Button
    private lateinit var vStop: Button

    private val codec = Opus()
    private val SAMPLE_RATE = Constants.SampleRate._8000()
    private val CHANNELS = Constants.Channels.stereo()
    /** "CHUNK_SIZE = FRAME_SIZE.v * CHANNELS.v * 2" it's formula from opus.h "frame_size*channels*sizeof(opus_int16)" */
    private val CHUNK_SIZE = Constants.FrameSize._160().v * CHANNELS.v * 2
    private val FRAME_SIZE_SHORT = Constants.FrameSize._320() // samples per channel
    private val FRAME_SIZE_BYTE = Constants.FrameSize._160()  // samples per channel
    private val APPLICATION = Constants.Application.voip()

    private var runLoop = false
    private var runWithShorts = true
    private var needToConvert = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vPlay = findViewById(R.id.vPlay)
        vStop = findViewById(R.id.vStop)

        vPlay.setOnClickListener {
            vPlay.visibility = View.GONE
            vStop.visibility = View.VISIBLE

            codec.encoderInit(SAMPLE_RATE, CHANNELS, APPLICATION)
            codec.decoderInit(SAMPLE_RATE, CHANNELS)

            requestPermissions()
        }

        vStop.setOnClickListener {
            stopRecording()
        }
    }

    private fun stopRecording() {
        vStop.visibility = View.GONE
        vPlay.visibility = View.VISIBLE
        stopLoop()
        ControllerAudio.stopRecord()
        ControllerAudio.stopTrack()
    }

    private fun startLoop() {
        stopLoop()
        runLoop = true
        ControllerAudio.initRecorder(SAMPLE_RATE.v, CHUNK_SIZE, CHANNELS.v == 1)
        ControllerAudio.initTrack(SAMPLE_RATE.v, CHANNELS.v == 1)
        ControllerAudio.startRecord()
        Thread {
            while (runLoop) { if (runWithShorts) processShorts() else processBytes() }
            if (!runLoop) {
                codec.encoderRelease()
                codec.decoderRelease()
            }
        }.start()
    }

    private fun stopLoop() {
        runLoop = false
    }

    private fun processShorts() {
        val frame = ControllerAudio.getFrameShort() ?: return
        val encoded = codec.encode(frame, FRAME_SIZE_SHORT) ?: return
        Log.d(TAG, "encoded: ${frame.size} shorts of audio into ${encoded.size} shorts")
        val decoded = codec.decode(encoded, FRAME_SIZE_SHORT) ?: return
        Log.d(TAG, "decoded: ${decoded.size} shorts")

        if (needToConvert) {
            val converted = codec.convert(decoded) ?: return
            Log.d(TAG, "converted: ${decoded.size} shorts into ${converted.size} bytes")
            ControllerAudio.write(converted)
        } else ControllerAudio.write(decoded)
        Log.d(TAG, "===========================================")
    }

    private fun processBytes() {
        val frame = ControllerAudio.getFrame() ?: return
        val encoded = codec.encode(frame, FRAME_SIZE_BYTE) ?: return
        Log.d(TAG, "encoded: ${frame.size} bytes of audio into ${encoded.size} bytes")
        val decoded = codec.decode(encoded, FRAME_SIZE_BYTE) ?: return
        Log.d(TAG, "decoded: ${decoded.size} bytes")

        if (needToConvert) {
            val converted = codec.convert(decoded) ?: return
            Log.d(TAG, "converted: ${decoded.size} bytes into ${converted.size} shorts")
            ControllerAudio.write(converted)
        } else ControllerAudio.write(decoded)
        Log.d(TAG, "===========================================")
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) startLoop()
        else if (checkSelfPermission(audioPermission) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(readPermission) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(writePermission) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(audioPermission, readPermission, writePermission), 123)
        } else startLoop()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (permissions[0] == audioPermission &&
            permissions[1] == readPermission &&
            permissions[3] == writePermission &&
            requestCode == 123) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                grantResults[2] == PackageManager.PERMISSION_GRANTED) startLoop()
            else Toast.makeText(this, "App doesn't have enough permissions to continue", Toast.LENGTH_LONG).show()
        }
    }

    override fun onPause() {
        super.onPause()
        stopRecording()
    }
}

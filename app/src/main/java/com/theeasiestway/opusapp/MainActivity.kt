package com.theeasiestway.opusapp

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.theeasiestway.opus.Constants
import com.theeasiestway.opus.Opus
import com.theeasiestway.opusapp.mic.ControllerAudio

class MainActivity : AppCompatActivity() {

    private val audioPermission = android.Manifest.permission.RECORD_AUDIO
    private val readPermission = android.Manifest.permission.READ_EXTERNAL_STORAGE
    private val writePermission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE

    private lateinit var vPlay: Button
    private lateinit var vStop: Button

    private val codec = Opus()
    private val SAMPLE_RATE = Constants.SampleRate.sr_8000()
    private val FRAME_SIZE = 160
    private var runLoop = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vPlay = findViewById(R.id.vPlay)
        vStop = findViewById(R.id.vStop)

        ControllerAudio.initRecorder(SAMPLE_RATE.v, FRAME_SIZE * 2)
        ControllerAudio.initTrack(SAMPLE_RATE.v)

        vPlay.setOnClickListener {
            vPlay.visibility = View.GONE
            vStop.visibility = View.VISIBLE

            codec.encoderInit(SAMPLE_RATE, Constants.Channels.mono(), Constants.Application.audio())
            codec.decoderInit(SAMPLE_RATE, Constants.Channels.mono())

            ControllerAudio.startRecord()
            requestPermissions()
        }

        vStop.setOnClickListener {
            vStop.visibility = View.GONE
            vPlay.visibility = View.VISIBLE
            stopLoop()
            ControllerAudio.stopRecord()
        }
    }

    private fun startLoop() {
        stopLoop()
        runLoop = true
        Thread {
            while (runLoop) {
                val frame = ControllerAudio.getFrame() ?: continue
                val encoded = codec.encode(frame, frame.size, frame.size / 2) ?: continue
                Log.d("rgre", "encoded: ${frame.size} ${ if (frame is ByteArray) " bytes" else " shorts"} of audio into ${encoded.size} ${ if (encoded is ByteArray) "bytes " else "shorts "}")
                val decoded = codec.decode(encoded, encoded.size, frame.size / 2) ?: continue
                Log.d("rgre", "decoded: ${decoded.size}  ${ if (decoded is ByteArray) "bytes" else "shorts"}")
                ControllerAudio.write(decoded)
            }
            if (!runLoop) {
                codec.encoderRelease()
                codec.decoderRelease()
            }
        }.start()
    }

    private fun stopLoop() {
        runLoop = false
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
}

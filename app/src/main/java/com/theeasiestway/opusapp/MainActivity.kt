package com.theeasiestway.opusapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.theeasiestway.opus.Opus
import com.theeasiestway.opusapp.mic.ControllerAudio
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var vPlay: Button
    lateinit var vStop: Button

    val codec = Opus()
    val SAMPLE_RATE = 8000
    val FRAME_SIZE = 120
    var runned = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vPlay = findViewById(R.id.vPlay)
        vStop = findViewById(R.id.vStop)

        ControllerAudio.initRecorder(SAMPLE_RATE, FRAME_SIZE)
        ControllerAudio.initTrack(SAMPLE_RATE)

        codec.encoderInit(SAMPLE_RATE, 1, 1)
        codec.decoderInit(SAMPLE_RATE, 1)

        vPlay.setOnClickListener {
            vPlay.visibility = View.GONE
            vStop.visibility = View.VISIBLE
            ControllerAudio.startRecord()
            startLoop()
        }

        vStop.setOnClickListener {
            vStop.visibility = View.GONE
            vPlay.visibility = View.VISIBLE
            stopLoop()
        }
    }

    private fun startLoop() {
        stopLoop()
        runned = true
        val thread = Thread {
            while (runned) {
                val frame = ControllerAudio.getFrame() ?: continue
                val encoded = codec.encode(frame, frame.size, frame.size) ?: continue
                val decoded = codec.decode(encoded, encoded.size, encoded.size) ?: continue
                ControllerAudio.write(decoded)
            }
        }.start()
    }

    private fun stopLoop() {
        runned = false
    }
}

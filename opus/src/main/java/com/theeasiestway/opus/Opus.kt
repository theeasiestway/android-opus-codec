package com.theeasiestway.opus

import android.util.Log

class Opus {

    companion object {

        val TAG = "Opus"

        init {
            try {
                System.loadLibrary()
            } catch (e: Exception) {
                Log.e(TAG, "Couldn't load opus library: $e")
            }
        }
    }
}

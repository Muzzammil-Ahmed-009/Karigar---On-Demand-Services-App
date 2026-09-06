package com.karigar.app.utils

import android.media.MediaPlayer
import java.io.IOException

class AudioPlayerHelper {
    private var mediaPlayer: MediaPlayer? = null
    var isPlaying = false
        private set

    fun play(filePath: String, onCompletion: () -> Unit) {
        if (isPlaying) {
            stop()
        }
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(filePath)
                prepare()
                start()
                this@AudioPlayerHelper.isPlaying = true
                setOnCompletionListener {
                    this@AudioPlayerHelper.isPlaying = false
                    onCompletion()
                    release()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun stop() {
        mediaPlayer?.release()
        mediaPlayer = null
        isPlaying = false
    }
}

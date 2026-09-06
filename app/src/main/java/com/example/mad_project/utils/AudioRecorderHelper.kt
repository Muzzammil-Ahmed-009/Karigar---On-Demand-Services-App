package com.karigar.app.utils

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.io.IOException

class AudioRecorderHelper(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: File? = null

    fun startRecording(): String? {
        outputFile = File(context.cacheDir, "audio_record_${System.currentTimeMillis()}.m4a")
        
        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
        
        return try {
            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputFile?.absolutePath)
                prepare()
                start()
            }
            outputFile?.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun stopRecording(): String? {
        try {
            mediaRecorder?.stop()
        } catch (e: RuntimeException) {
            // Can happen if stop is called immediately after start
        }
        mediaRecorder?.release()
        mediaRecorder = null
        return outputFile?.absolutePath
    }
}

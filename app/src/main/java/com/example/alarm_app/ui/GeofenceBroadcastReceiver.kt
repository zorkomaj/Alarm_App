package com.example.alarm_app.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import com.example.alarm_app.R
import java.io.File

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onReceive(context: Context, intent: Intent) {
        playAlarmSound(context)
    }

    private fun playAlarmSound(context: Context) {
        val alarmsoundFile = File(context.filesDir, "alarmsound.mp3")

        if (alarmsoundFile.exists()) {
            try {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(alarmsoundFile.absolutePath)
                    prepare()
                    start()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
            }
        } else {
            mediaPlayer = MediaPlayer.create(context, R.raw.alarmsound)
            mediaPlayer?.start()
        }
    }
}

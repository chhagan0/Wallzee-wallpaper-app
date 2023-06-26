package com.adarsh.wallzee.ringtone

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log.d
import android.view.View
import android.widget.Toast
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.io.IOException


class RingtoneFunctions {
    private var mediaPlayer: MediaPlayer? = null
    var isplay = false

    fun playAudio(
        ringtoneUrl: String?,
        btRingtonePlay: MaterialButton,
        btRingtonePause: MaterialButton,
        circularProgressIndicator: CircularProgressIndicator?,
        lineaProgressIndicator: LinearProgressIndicator?
    ) {
        val handler = Handler(Looper.getMainLooper())

//        mediaPlayer?.release() // Release any previously initialized media player

        handler.post {
            btRingtonePlay.visibility = View.INVISIBLE
            btRingtonePause.visibility = View.VISIBLE
        }

        if (circularProgressIndicator != null) {
            handler.post {
                circularProgressIndicator.visibility = View.VISIBLE
//            circularProgressIndicator.progress = 50
            }
        }

        if (lineaProgressIndicator != null) {
            handler.post {
                lineaProgressIndicator.visibility = View.VISIBLE
//            lineaProgressIndicator.progress = 50
            }
        }

        mediaPlayer = MediaPlayer()
        mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)

        try {
             mediaPlayer?.setDataSource(ringtoneUrl)
            mediaPlayer?.prepare()
            mediaPlayer?.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun pauseAudio(

        btRingtonePause: MaterialButton,
        btRingtonePlay: MaterialButton,
        context: Context?,
        circularProgressIndicator: CircularProgressIndicator?,
        linearProgressIndicator: LinearProgressIndicator?
    ) {

        if (mediaPlayer != null && mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()

            btRingtonePause.visibility = View.INVISIBLE
            btRingtonePlay.visibility = View.VISIBLE

            if (circularProgressIndicator != null) {
                circularProgressIndicator.progress = 100
                circularProgressIndicator.visibility = View.INVISIBLE
            }

            if (linearProgressIndicator != null) {
                linearProgressIndicator.progress = 100
                linearProgressIndicator.visibility = View.INVISIBLE
            }
        }
    }

    fun releaseMediaPlayer() {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.pause()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        d("CHAGAN", "Media player stopped")
    }


}


package com.example.playlistmaker.player.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.playlistmaker.R
import java.text.SimpleDateFormat
import java.util.Locale

class MusicPlayerService : Service() {

    inner class LocalBinder : Binder() {
        fun getService(): MusicPlayerService = this@MusicPlayerService
    }

    private val binder = LocalBinder()

    private lateinit var mediaPlayer: MediaPlayer
    private var prepared = false
    private var onPreparedCallback: (() -> Unit)? = null
    private var onCompletionCallback: (() -> Unit)? = null

    private val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
    private var artist: String = ""
    private var title: String = ""

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        mediaPlayer = MediaPlayer().apply {
            setOnPreparedListener {
                prepared = true
                onPreparedCallback?.invoke()
            }
            setOnCompletionListener {
                seekTo(0)
                onCompletionCallback?.invoke()
                stopForegroundIfAny()
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder = binder

    fun setDataSource(url: String) {
        prepared = false
        mediaPlayer.reset()
        mediaPlayer.setDataSource(url)
    }

    fun prepare(onPrepared: () -> Unit) {
        onPreparedCallback = onPrepared
        mediaPlayer.prepareAsync()
    }

    fun setOnCompletionListener(onComplete: () -> Unit) {
        onCompletionCallback = onComplete
    }

    fun start() {
        if (!prepared) {
            prepare {
                mediaPlayer.start()
            }
            return
        }
        mediaPlayer.start()
    }

    fun pause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            stopForegroundIfAny()
        }
    }

    fun stop() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            prepared = false
            stopForegroundIfAny()
        }
    }

    fun seekTo(position: Int) {
        if (prepared) {
            mediaPlayer.seekTo(position)
        }
    }

    fun isPlaying(): Boolean = mediaPlayer.isPlaying

//    fun isPrepared(): Boolean = prepared

    fun getCurrentTimeFormatted(): String = dateFormat.format(mediaPlayer.currentPosition)

    fun setNotificationMeta(artistName: String, trackTitle: String) {
        artist = artistName
        title = trackTitle
    }

    fun startForegroundIfPlaying() {
        if (!isPlaying()) return
        val notification = buildNotification("$artist - $title")
        startForeground(NOTIF_ID, notification)
    }

    fun stopForegroundIfAny() {
        try {
            if (Build.VERSION.SDK_INT >= 24) {
                stopForeground(STOP_FOREGROUND_REMOVE)
            } else {
                @Suppress("DEPRECATION")
                stopForeground(true)
            }
        } catch (_: Throwable) { }
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(NOTIF_ID)
    }

    private fun buildNotification(content: String): Notification {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pi = PendingIntent.getActivity(
            this, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.favorite_true)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(content)
            .setContentIntent(pi)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.playback_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply { setShowBadge(false) }
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        try {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
        } catch (_: Throwable) { }
        stopForegroundIfAny()
        stopSelf()
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        try {
            if (this::mediaPlayer.isInitialized) {
                if (mediaPlayer.isPlaying) mediaPlayer.stop()
                mediaPlayer.release()
            }
        } catch (_: Throwable) { }
        super.onDestroy()
    }

    companion object {
        private const val CHANNEL_ID = "playback"
        private const val NOTIF_ID = 1001
    }
}
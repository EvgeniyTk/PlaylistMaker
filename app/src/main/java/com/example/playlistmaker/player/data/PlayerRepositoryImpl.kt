package com.example.playlistmaker.player.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.example.playlistmaker.player.domain.PlayerRepository
import com.example.playlistmaker.player.service.MusicPlayerService
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.CopyOnWriteArrayList

class PlayerRepositoryImpl(private val context: Context) : PlayerRepository {

    private var service: MusicPlayerService? = null
    private var bound = false

    private var prepared = false
    private val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())

    private val pendingActions = CopyOnWriteArrayList<(MusicPlayerService) -> Unit>()

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            service = (binder as MusicPlayerService.LocalBinder).getService()
            bound = true
            val svc = service ?: return
            pendingActions.forEach { it.invoke(svc) }
            pendingActions.clear()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            service = null
            bound = false
        }
    }

    override fun bindService() {
        if (bound) return
        val intent = Intent(context, MusicPlayerService::class.java)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun unbindService() {
        if (bound) {
            context.unbindService(connection)
            bound = false
        }
        service = null
        prepared = false
        pendingActions.clear()
    }

    private fun runOrQueue(action: (MusicPlayerService) -> Unit) {
        val current = service
        if (current != null && bound) {
            action(current)
        } else {
            pendingActions.add(action)
            if (!bound) {
                bindService()
            }
        }
    }

    override fun setDataSource(url: String) {
        prepared = false
        runOrQueue { it.setDataSource(url) }
    }

    override fun prepare(onPrepared: () -> Unit) {
        runOrQueue { svc ->
            svc.prepare {
                prepared = true
                onPrepared()
            }
        }
    }

    override fun setOnCompletionListener(onComplete: () -> Unit) {
        runOrQueue { it.setOnCompletionListener(onComplete) }
    }

    override fun start() {
        runOrQueue { it.start() }
    }

    override fun pause() {
        val svc = service
        if (svc != null && bound) {
            svc.pause()
        }
    }

    override fun stop() {
        val svc = service
        if (svc != null && bound) {
            svc.stop()
            prepared = false
        }
    }

    override fun seekTo(position: Int) {
        val svc = service
        if (svc != null && bound) {
            svc.seekTo(position)
        }
    }

    override fun release() {
        try {
            service?.stopForegroundIfAny()
            service?.stop()
        } catch (_: Throwable) { }
        unbindService()
    }

    override fun isPlaying(): Boolean = service?.isPlaying() == true

    override fun isPrepared(): Boolean = prepared

    override fun formatTime(time: Int): String = dateFormat.format(time)

    override fun getCurrentTimeFormatted(): String =
        service?.getCurrentTimeFormatted() ?: dateFormat.format(0)

    override fun setNotificationMeta(artist: String, title: String) {
        runOrQueue { it.setNotificationMeta(artist, title) }
    }

    override fun startForegroundIfPlaying() {
        val svc = service
        if (svc != null && bound) {
            svc.startForegroundIfPlaying()
        }
    }

    override fun stopForegroundIfAny() {
        val svc = service
        if (svc != null && bound) {
            svc.stopForegroundIfAny()
        }
    }
}
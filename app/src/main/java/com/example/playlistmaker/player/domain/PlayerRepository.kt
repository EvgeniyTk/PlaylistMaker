package com.example.playlistmaker.player.domain

interface PlayerRepository {
    fun setDataSource(url: String)
    fun prepare(onPrepared: () -> Unit)
    fun setOnCompletionListener(onComplete: () -> Unit)
    fun start()
    fun pause()
    fun stop()
    fun seekTo(position: Int)
    fun release()
    fun isPlaying(): Boolean
    fun isPrepared(): Boolean
    fun formatTime(time: Int): String
    fun getCurrentTimeFormatted(): String
    fun setNotificationMeta(artist: String, title: String)
    fun startForegroundIfPlaying()
    fun stopForegroundIfAny()
    fun bindService()
    fun unbindService()
}
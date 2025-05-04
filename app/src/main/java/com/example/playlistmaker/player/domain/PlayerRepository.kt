package com.example.playlistmaker.player.domain


interface PlayerRepository {
    fun setDataSource(url: String)
    fun prepare(onPrepared: () -> Unit)
    fun setOnCompletionListener(onComplete: () -> Unit)
    fun start()
    fun pause()
    fun release()
    fun isPlaying(): Boolean
    fun isPrepared(): Boolean
    fun getCurrentTimeFormatted(): String
}
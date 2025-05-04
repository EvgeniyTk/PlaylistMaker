package com.example.playlistmaker.player.model



sealed class PlayerState {
    data class Prepared(val initialTime: String) : PlayerState()
    object Playing : PlayerState()
    object Paused : PlayerState()
    data class TimeUpdated(val time: String) : PlayerState()
}
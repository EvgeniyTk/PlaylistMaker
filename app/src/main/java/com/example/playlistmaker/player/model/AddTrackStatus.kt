package com.example.playlistmaker.player.model


sealed interface AddTrackStatus {
        data class Success(val playlistName: String): AddTrackStatus
        data class AlreadyAdded(val playlistName: String): AddTrackStatus

}
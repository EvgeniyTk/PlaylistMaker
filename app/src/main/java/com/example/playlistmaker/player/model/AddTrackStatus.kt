package com.example.playlistmaker.player.model


interface AddTrackStatus {
        data class Success(val playlistName: String): AddTrackStatus
        data class AlreadyAdded(val playlistName: String): AddTrackStatus

}
package com.example.playlistmaker.library.domain.model


data class Playlist(
    val playlistId: Int = 0,
    val playlistName: String,
    val playlistDescription: String = "",
    val imagePath: String? = null,
    val trackIdList: List<Int> = mutableListOf(),
    val tracksCount: Int = 0
)

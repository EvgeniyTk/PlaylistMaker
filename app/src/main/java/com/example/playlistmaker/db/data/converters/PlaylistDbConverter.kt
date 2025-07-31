package com.example.playlistmaker.db.data.converters

import com.example.playlistmaker.db.data.entity.PlaylistEntity
import com.example.playlistmaker.library.domain.model.Playlist
import com.google.gson.Gson

class PlaylistDbConverter {
    private val gson = Gson()

    fun map(playlist: Playlist): PlaylistEntity {
        val trackIdListJson = gson.toJson(playlist.trackIdList)
        return PlaylistEntity(
            playlist.playlistId,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.imagePath,
            trackIdListJson,
            playlist.tracksCount
        )
    }

    fun map(playlistEntity: PlaylistEntity): Playlist {
        val trackIdList = mutableListOf<Int>().apply {
            addAll(gson.fromJson(playlistEntity.trackIdList, Array<Int>::class.java))
        }
        return Playlist(
            playlistEntity.playlistId,
            playlistEntity.playlistName,
            playlistEntity.playlistDescription,
            playlistEntity.imagePath,
            trackIdList,
            playlistEntity.tracksCount
        )
    }
}

package com.example.playlistmaker.db.data.converters

import com.example.playlistmaker.db.data.entity.PlaylistTrackEntity
import com.example.playlistmaker.search.domain.models.Track

class PlaylistTrackDbConverter {
    fun map(track: Track): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl
        )
    }
}
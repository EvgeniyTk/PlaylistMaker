package com.example.playlistmaker.search.data.mapper

import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.domain.models.Track

object TrackMapper {
    fun map(dto: TrackDto): Track {
        return Track(
            trackId = dto.trackId ?: 0,
            trackName = dto.trackName ?: "",
            artistName = dto.artistName ?: "",
            trackTimeMillis = dto.trackTimeMillis ?: 0,
            artworkUrl100 = dto.artworkUrl100 ?: "",
            collectionName = dto.collectionName ?: "",
            releaseDate = dto.releaseDate ?: "",
            primaryGenreName = dto.primaryGenreName ?: "",
            country = dto.country ?: "",
            previewUrl = dto.previewUrl ?: ""
        )
    }
}
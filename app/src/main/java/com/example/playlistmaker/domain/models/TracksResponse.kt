package com.example.playlistmaker.domain.models

import com.example.playlistmaker.data.dto.ResponseType

data class TracksResponse(val trackList: List<Track>?, val responseType: ResponseType)

package com.example.playlistmaker.search.domain.models

import com.example.playlistmaker.search.data.dto.ResponseType

data class TracksResponse(val trackList: List<Track>?, val responseType: ResponseType)

package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.ResponseType
import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.data.dto.TrackSearchResponse
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.data.mapper.TrackMapper
import com.example.playlistmaker.domain.models.TracksResponse

class TracksRepositoryImpl(private val networkClient: NetworkClient): TracksRepository {
    override fun searchTracks(expression: String): TracksResponse {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        if (response.responseType == ResponseType.SUCCESS) {
            val trackList = (response as TrackSearchResponse).results.map {
                TrackMapper.map(it)
            }
            return TracksResponse(trackList, ResponseType.SUCCESS)

        } else {
            return TracksResponse(null, ResponseType.BAD_CONNECTION)
        }
    }

}
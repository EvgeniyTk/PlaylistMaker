package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.dto.ResponseType
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.data.dto.TrackSearchResponse
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.data.mapper.TrackMapper
import com.example.playlistmaker.search.domain.models.TracksResponse

class TracksRepositoryImpl(private val networkClient: NetworkClient): TracksRepository {
    override fun searchTracks(expression: String): TracksResponse {
        val response = networkClient.doRequest(TrackSearchRequest(expression))

        return when (response.responseType) {
            ResponseType.SUCCESS -> {
                val trackList = (response as TrackSearchResponse).results.map {
                    TrackMapper.map(it)
                }
                TracksResponse(trackList, ResponseType.SUCCESS)
            }

            ResponseType.BAD_CONNECTION -> TracksResponse(null, ResponseType.BAD_CONNECTION)
            ResponseType.BAD_REQUEST -> TracksResponse(null, ResponseType.BAD_REQUEST)
            ResponseType.UNKNOWN -> TracksResponse(null, ResponseType.UNKNOWN)
        }
    }

}
package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.dto.ResponseType
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.data.dto.TrackSearchResponse
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.data.mapper.TrackMapper
import com.example.playlistmaker.search.domain.models.TracksResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(private val networkClient: NetworkClient): TracksRepository {
    override fun searchTracks(expression: String): Flow<TracksResponse> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(expression))

        when (response.responseType) {
            ResponseType.SUCCESS -> {
                val trackList = (response as TrackSearchResponse).results.map {
                    TrackMapper.map(it)
                }
                emit(TracksResponse(trackList, ResponseType.SUCCESS))
            }

            ResponseType.BAD_CONNECTION -> emit(TracksResponse(null, ResponseType.BAD_CONNECTION))
            ResponseType.BAD_REQUEST -> emit(TracksResponse(null, ResponseType.BAD_REQUEST))
            ResponseType.UNKNOWN -> emit(TracksResponse(null, ResponseType.UNKNOWN))
        }
    }

}
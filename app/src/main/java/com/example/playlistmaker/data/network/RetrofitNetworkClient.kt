package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.ResponseType
import com.example.playlistmaker.data.dto.TrackSearchRequest

class RetrofitNetworkClient(private val iTunesService: ITunesApi) : NetworkClient {


    override fun doRequest(dto: Any): Response {
        return try {
            if (dto is TrackSearchRequest) {
                val resp = iTunesService.search(dto.expression).execute()
                val body = resp.body() ?: Response()

                 body.apply { responseType = ResponseType.SUCCESS }
            } else {
                Response().apply { responseType = ResponseType.BAD_CONNECTION }
            }
        } catch (e: Exception) {
            Response().apply { responseType = ResponseType.BAD_CONNECTION }
        }


    }
}
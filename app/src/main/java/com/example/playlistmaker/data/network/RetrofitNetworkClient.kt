package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.ResponseType
import com.example.playlistmaker.data.dto.TrackSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val iTunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesApi::class.java)

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
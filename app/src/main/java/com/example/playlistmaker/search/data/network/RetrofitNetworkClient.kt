package com.example.playlistmaker.search.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.ResponseType
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(private val iTunesService: ITunesApi, private val context: Context) :
    NetworkClient {


    override suspend fun doRequest(dto: Any): Response {


        if (!isConnected()) {
            return Response().apply { responseType = ResponseType.BAD_CONNECTION }
        }
        if (dto !is TrackSearchRequest) {
            return Response().apply { responseType = ResponseType.BAD_REQUEST }
        }

        return withContext(Dispatchers.IO) {
            try {
                val resp = iTunesService.search(dto.expression)
                resp.apply { responseType = ResponseType.SUCCESS }

            } catch (e: Throwable) {
                Response().apply { responseType = ResponseType.BAD_CONNECTION }
            }

        }

    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}
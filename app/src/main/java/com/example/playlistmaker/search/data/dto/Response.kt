package com.example.playlistmaker.search.data.dto

enum class ResponseType {
    SUCCESS,
    BAD_CONNECTION,
    BAD_REQUEST,
    UNKNOWN
}

open class Response() {
    var responseType = ResponseType.UNKNOWN
}
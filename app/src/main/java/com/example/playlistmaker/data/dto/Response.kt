package com.example.playlistmaker.data.dto

enum class ResponseType {
    SUCCESS,
    BAD_CONNECTION,
    UNKNOWN
}

open class Response() {
    var responseType = ResponseType.UNKNOWN

}
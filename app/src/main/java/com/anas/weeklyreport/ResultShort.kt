package com.anas.weeklyreport



sealed class ResultShort<out R>{
    data class Success<out T>(val data:T): ResultShort<T>()
    data class Error(val exception: Exception): ResultShort<Nothing>()
    data class NetworkError(val exception: Exception): ResultShort<Nothing>()
}



package com.anas.weeklyreport.model

data class MyServerResponse<T>(
    var data: T? = null,
    var message:String? = "",
)
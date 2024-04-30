package com.anas.weeklyreport.model

data class MyServerResponse<T>(
    var data: T? = null,
    var status: Int = 0,
    var message:String? = "",
)
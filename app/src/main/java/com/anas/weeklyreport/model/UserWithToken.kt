package com.anas.weeklyreport.model

data class UserWithToken(
    var token:String = "",
    var user: User = User()
)

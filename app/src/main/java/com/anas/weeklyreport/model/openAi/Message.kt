package com.anas.weeklyreport.model.openAi

import java.time.LocalTime


data class Message(
    val id:String,
    var chatId:String,
    var content:String,
    var role:String,
    var createdAt:String = LocalTime.now().toString(),
    var isContentLoading:Boolean = false,

)

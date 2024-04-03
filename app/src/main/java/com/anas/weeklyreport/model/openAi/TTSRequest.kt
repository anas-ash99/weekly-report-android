package com.anas.aiassistant.model.openAi

data class TTSRequest(
    val model:String,
    val input:String,
    val voice:String,
)
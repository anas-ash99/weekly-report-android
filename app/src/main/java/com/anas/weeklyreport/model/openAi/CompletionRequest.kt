package com.anas.weeklyreport.model.openAi


data class CompletionRequest(
    val model: String,
    val messages: List<ChatGBTMessage>
)

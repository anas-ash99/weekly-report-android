package com.anas.weeklyreport.model.openAi



data class Choice(
    val index: Int,
    val message: Message,
    val logprobs: Any?,
    val finishReason: String
)

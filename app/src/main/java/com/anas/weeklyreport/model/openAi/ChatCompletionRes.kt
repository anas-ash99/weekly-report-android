package com.anas.aiassistant.model.openAi

import com.anas.weeklyreport.model.openAi.Choice

data class ChatCompletionRes(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val systemFingerprint: String,
    val choices: List<Choice>,
    val usage: Usage
)

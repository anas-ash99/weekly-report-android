package com.anas.weeklyreport.remote

import com.anas.aiassistant.model.openAi.ChatCompletionRes
import com.anas.weeklyreport.model.openAi.CompletionRequest
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenAiApi {

    @POST("chat/completions")
    suspend fun generateCompletion(
        @Header("Authorization") apiKey: String,
        @Body request: CompletionRequest,
        @Header("Content-Type") contentType: String = "application/json"
    ): ChatCompletionRes

}
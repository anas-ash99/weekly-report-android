package com.anas.weeklyreport.remote

import com.anas.aiassistant.model.openAi.ChatCompletionRes
import com.anas.weeklyreport.model.Email
import com.anas.weeklyreport.model.MyServerResponse
import com.anas.weeklyreport.model.openAi.CompletionRequest
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface EmailAPI {
    @POST("emails")
    suspend fun sendEmail(
        @Body email: Email,
    ): MyServerResponse<Email>

}
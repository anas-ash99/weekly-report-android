package com.anas.weeklyreport.data.repository

import com.anas.weeklyreport.ResultShort
import com.anas.weeklyreport.data.Result
import com.anas.weeklyreport.model.Email
import com.anas.weeklyreport.model.User
import kotlinx.coroutines.flow.Flow

interface EmailRepository {
    suspend fun sendEmail(email: Email):Flow<Result<Email>>
}
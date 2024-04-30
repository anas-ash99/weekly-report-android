package com.anas.weeklyreport.data.repository

import android.util.Log
import com.anas.weeklyreport.data.Result
import com.anas.weeklyreport.model.Email
import com.anas.weeklyreport.remote.EmailAPI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EmailRepositoryImpl @Inject constructor(
    private val emailAPI: EmailAPI
): EmailRepository {
    override suspend fun sendEmail(email: Email): Flow<Result<Email>> = flow {
        emit(Result.Loading)
        try {
            if (email.toEmail.isBlank()){
                email.toEmail = "weekly.report0@gmail.com"
            }
            val res = emailAPI.sendEmail(email)
            if (res.data != null){
                emit(Result.Success(res.data!!))
            }else{
                emit(Result.Error(Exception(res.message)))
            }
        }catch (e:Exception){
            Log.e("send email", e.message, e)
            emit(Result.Error(e))
        }
    }
}
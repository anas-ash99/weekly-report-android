package com.anas.weeklyreport.data.repository

import com.anas.weeklyreport.ResultShort
import com.anas.weeklyreport.data.Result
import com.anas.weeklyreport.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun signUp(user: User):Flow<Result<User>>
    suspend fun signOut()
    suspend fun updateUser(user: User):Flow<Result<User>>
    fun getUser():User?
    suspend fun deleteUserById(id:String):Flow<Result<Boolean>>
    suspend fun checkIfUserExist(id:String):Flow<Result<Boolean>>
}
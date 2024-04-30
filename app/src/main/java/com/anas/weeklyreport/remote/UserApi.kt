package com.anas.weeklyreport.remote

import com.anas.weeklyreport.model.MyServerResponse
import com.anas.weeklyreport.model.User
import com.anas.weeklyreport.model.UserWithToken
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {
    @POST("users")
    suspend fun createNewUser(@Body user: User): MyServerResponse<UserWithToken>
    @PUT("users")
    suspend fun updateUser(@Body user: User): MyServerResponse<Boolean>
    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id:String): MyServerResponse<Boolean>
    @GET("users/check-if-exist/{id}")
    suspend fun checkIfUserExist(@Path("id") id:String): MyServerResponse<Boolean>
}
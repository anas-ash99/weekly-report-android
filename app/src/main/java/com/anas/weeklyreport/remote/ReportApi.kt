package com.anas.weeklyreport.remote

import com.anas.weeklyreport.model.DocumentBody
import com.anas.weeklyreport.model.MyServerResponse
import com.anas.weeklyreport.model.Report
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface ReportApi {
     @POST("reports")
     suspend fun createNewReport(@Body report: Report, @Header("Authorization") userToken: String): MyServerResponse<Report>
     @PUT("reports")
     suspend fun updateReport(@Body report: Report, @Header("Authorization") userToken: String): MyServerResponse<Boolean>
     @GET("reports/{id}")
     suspend fun getReportById(@Path("id") id:String, @Header("Authorization") userToken: String) : MyServerResponse<Report>
     @GET("reports/by-user/{id}")
     suspend fun getReportByUserId(@Path("id") id:String, @Header("Authorization") userToken: String) : MyServerResponse<List<Report>>

     @POST("documents")
     suspend fun getDocument(@Body documentBody: DocumentBody):ResponseBody

     @DELETE("reports/{id}")
     suspend fun deleteReport(@Path("id") id:String, @Header("Authorization") userToken: String):MyServerResponse<Boolean>

}

//Response<ResponseBody>

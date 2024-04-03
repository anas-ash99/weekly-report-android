package com.anas.weeklyreport.remote

import com.anas.weeklyreport.model.MyServerResponse
import com.anas.weeklyreport.model.Report
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface ReportApi {

    @POST("reports")
    suspend fun createNewReport(@Body report: Report): MyServerResponse<Report>
     @GET("reports/{id}")
     suspend fun getReportById(@Path("id") id:String) : MyServerResponse<Report>

     @GET("reports")
     suspend fun getAllReports():MyServerResponse<ArrayList<Report>>

     @GET("reports/documents/{id}")
     suspend fun getDocument(@Path("id") reportId:String):ResponseBody

     @DELETE("reports/{id}")
     suspend fun deleteReport(@Path("id") id:String):MyServerResponse<Boolean>

}

//Response<ResponseBody>

package com.anas.weeklyreport.data

import com.anas.weeklyreport.ResultShort
import com.anas.weeklyreport.model.Report
import com.anas.weeklyreport.model.User
import okhttp3.ResponseBody

interface RemoteDataSource {
    suspend fun saveReport(report: Report): ResultShort<Report>
    suspend fun updateReport(report: Report): ResultShort<Boolean>
    suspend fun getReportsByUserId():ResultShort<List<Report>>
    suspend fun deleteReport(id:String):ResultShort<Boolean>
    suspend fun getDocument(report: Report): ResponseBody
}
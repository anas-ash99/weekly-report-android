package com.anas.weeklyreport.data.repository

import android.net.Uri
import com.anas.weeklyreport.data.Result
import com.anas.weeklyreport.model.Report
import kotlinx.coroutines.flow.Flow


interface ReportRepository{
   suspend fun getAllReports():Flow<Result<Nothing?>>
   suspend fun createReport(report: Report):Flow<Result<Report>>
   suspend fun updateReport(report: Report):Flow<Result<Boolean>>
   suspend fun deleteReport(id: String):Flow<Result<Boolean>>
   suspend fun getDocument(report: Report):Flow<Result<Uri>>
   suspend fun getOpenAiChatResponse(prompt:String):Flow<Result<Report>>
   suspend fun deleteAllLocalReports()
   fun saveDataToLocalCache(value:String, key:String):Boolean
   fun getDataFromLocalCache(key:String):String?
}
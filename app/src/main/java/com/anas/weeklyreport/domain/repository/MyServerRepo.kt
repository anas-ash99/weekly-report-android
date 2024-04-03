package com.anas.weeklyreport.domain.repository

import android.net.Uri
import com.anas.weeklyreport.domain.DataState
import com.anas.weeklyreport.model.Report
import kotlinx.coroutines.flow.Flow


interface MyServerRepo {
   suspend fun getAllReports():Flow<DataState<ArrayList<Report>>>
   suspend fun saveReport(report: Report):Flow<DataState<Report>>
   suspend fun deleteReport(id: String):Flow<DataState<Boolean>>
   suspend fun updateReport(report: Report):Flow<DataState<Report>>
   suspend fun getDocument(reportId:String, fileName:String):Flow<DataState<Uri>>
   suspend fun getOpenAiChatResponse(prompt:String):Flow<DataState<Report>>
   fun saveDataToLocalCache(value:String, key:String):Flow<DataState<Boolean>>
   fun getDataFromLocalCache(key:String):String?
}
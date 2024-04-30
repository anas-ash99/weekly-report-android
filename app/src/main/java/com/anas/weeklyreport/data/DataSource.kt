package com.anas.weeklyreport.data

import com.anas.weeklyreport.model.Report

interface DataSource {
    suspend fun saveReport(report: Report)
    suspend fun getAllReports():List<Report>
    suspend fun deleteReport(id:String)
    suspend fun deleteAllReports()

}







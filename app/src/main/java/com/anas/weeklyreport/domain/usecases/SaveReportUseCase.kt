package com.anas.weeklyreport.domain.usecases

import com.anas.weeklyreport.data.DataState
import com.anas.weeklyreport.data.repository.MyServerRepo
import com.anas.weeklyreport.model.Report
import kotlinx.coroutines.flow.first

//class SaveReportUseCase(private val myServerRepo: MyServerRepo) {
//    suspend operator fun invoke(report: Report): DataState<Report> {
//        return try {
//            val savedReport = myServerRepo.saveReport(report).first()
//            DataState.Success(savedReport)
//        } catch (e: Exception) {
//            DataState.Error(exception = e)
//        }
//    }
//}
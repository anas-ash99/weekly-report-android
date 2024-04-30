package com.anas.weeklyreport.domain.usecases

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
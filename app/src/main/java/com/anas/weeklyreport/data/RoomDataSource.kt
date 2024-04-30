package com.anas.weeklyreport.data

import android.util.Log
import com.anas.weeklyreport.database.ReportDao
import com.anas.weeklyreport.database.entities.DescriptionEntity
import com.anas.weeklyreport.extension_methods.toReport
import com.anas.weeklyreport.extension_methods.toReportEntity
import com.anas.weeklyreport.model.Description
import com.anas.weeklyreport.model.Report
import javax.inject.Inject

class RoomDataSource @Inject constructor(
    private val reportDao: ReportDao,
):DataSource {
    override suspend fun saveReport(report: Report) {
        reportDao.insertReport(report.toReportEntity())
        report.weekdayDescription.forEach { weekday ->
            weekday.descriptions.forEach {  description ->
                reportDao.insertDescription(DescriptionEntity(
                    id = weekday.id,
                    reportId = report.id,
                    description = description.description,
                    weekday = weekday.day,
                    hours = description.hours,
                    ))
            }

        }
    }

    override suspend fun getAllReports(): List<Report> {
        val reports = arrayListOf<Report>()
        reportDao.getAllReports().forEach { dbReport ->
            val report = dbReport.toReport()
            reportDao.getDescriptionsById(dbReport.id).forEach { des ->
                if (dbReport.id == des.reportId){
                    report.weekdayDescription.forEach {  des2 ->
                        des2.id = des.id
                        if (des2.day.lowercase() == des.weekday.lowercase()){
                            des2.descriptions.add(Description(des.description, des.hours))
                        }
                    }
                }
            }
            reports.add(report)
        }
        return reports
    }

    override suspend fun deleteReport(id: String) {
        reportDao.deleteById(id)
        reportDao.deleteReportDescription(id)
    }

    override suspend fun deleteAllReports() {
        try {
            reportDao.deleteAllReports()
        }catch (e:Exception){
            Log.e("delete all reports locally", e.message, e)
        }
    }
}



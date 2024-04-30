package com.anas.weeklyreport.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.anas.weeklyreport.database.entities.DescriptionEntity
import com.anas.weeklyreport.database.entities.ReportEntity
import com.anas.weeklyreport.database.entities.WeekdayDescriptionEntity


@Dao
interface ReportDao {
    @Upsert
    suspend fun insertReport(vararg report: ReportEntity)
    @Upsert
    suspend fun insertWeekDay(vararg weekdayDescriptionEntity: WeekdayDescriptionEntity)
    @Upsert
    suspend fun insertDescription(vararg description: DescriptionEntity)
    @Insert
    suspend fun insertAll(report: List<ReportEntity>)
    @Query("DELETE FROM report")
    suspend fun deleteAllReports()
    @Query("SELECT * FROM report ORDER BY createdAt DESC")
    suspend fun getAllReports(): List<ReportEntity>
    @Query("DELETE FROM report WHERE id = :id")
    suspend fun deleteById(id:String)

    @Query("SELECT * FROM DescriptionEntity")
    suspend fun getAllDescriptions(): List<DescriptionEntity>
    @Query("SELECT * FROM DescriptionEntity WHERE reportId = :id")
    suspend fun getDescriptionsById(id:String): List<DescriptionEntity>

    @Query("DELETE FROM DescriptionEntity")
    suspend fun deleteAllDescriptions()
    @Query("DELETE FROM DescriptionEntity WHERE reportId == :reportId")
    suspend fun deleteReportDescription(reportId:String)
}
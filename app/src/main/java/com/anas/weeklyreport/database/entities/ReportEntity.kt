package com.anas.weeklyreport.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "report")
data class ReportEntity(
    @PrimaryKey
    var id:String = "",
    var name:String = "",
    var year:String = "",
    var calenderWeak:String = "",
    var fromDate:String = "",
    var toDate:String = "",
    var reportNumber:String = "",
    var isInTrash:Boolean = false,
    var isBookmarked:Boolean = false,
    var createdAt:String = "",
    var isDeleted:Boolean = false,
    var isSynced:Boolean = true,
    var userId:String = ""
)

@Entity
data class WeekdayDescriptionEntity(
    @PrimaryKey(autoGenerate = true) val weekdayId: Int = 0,
    val day: String,
    val reportId: String  // Foreign key reference to the Report
)

@Entity
data class DescriptionEntity(
    @PrimaryKey
    var id: String,
    val reportId: String,
    val description: String,
    val hours: String,
    val weekday: String, // Foreign key reference to WeekdayDescription
    var isSynced:Boolean = true
)

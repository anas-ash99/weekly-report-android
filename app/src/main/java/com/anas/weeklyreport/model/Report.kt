package com.anas.weeklyreport.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey



data class Report(
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
    var userId:String = "",
    var isSynced:Boolean = true,
   var weekdayDescription:List<Weekday> = listOf(
        Weekday(day = "monday"),
        Weekday(day = "tuesday"),
        Weekday(day = "wednesday"),
        Weekday(day = "thursday"),
        Weekday(day = "friday"),
    )
)

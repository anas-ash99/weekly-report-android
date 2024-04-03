package com.anas.weeklyreport.model

data class Report(
    var id:String = "",
    var name:String = "",
    var year:String = "",
    var calenderWeak:String = "",
    var fromDate:String = "",
    var toDate:String = "",
    var reportNumber:String = "",
    var weekdayDescription:List<Weekday> = listOf(
        Weekday(day = "monday"),
        Weekday(day = "tuesday"),
        Weekday(day = "wednesday"),
        Weekday(day = "thursday"),
        Weekday(day = "friday"),
    ),
    var isInTrash:Boolean = false,
    var isBookmarked:Boolean = false,
    var createdAt:String = ""
)

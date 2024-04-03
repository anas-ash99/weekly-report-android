package com.anas.weeklyreport.model

data class Weekday(
    var day:String = "",
    var descriptions:ArrayList<Description> = arrayListOf(),

    )
data class Description(
    var description: String = "",
    var hours: String = ""
)
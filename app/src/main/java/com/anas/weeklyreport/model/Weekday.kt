package com.anas.weeklyreport.model

import java.util.UUID


data class Weekday(
    var id:String  = UUID.randomUUID().toString(),
    var day:String = "",
    var descriptions:ArrayList<Description> = arrayListOf()
)

data class Description(
    var description: String = "",
    var hours: String = ""
)
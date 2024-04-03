package com.anas.weeklyreport.extension_methods

import com.anas.weeklyreport.shared.ReportListType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun String.reformatDate(pattern: String = "dd/MM/yyyy"): String {
    val originalFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val newFormatter = DateTimeFormatter.ofPattern(pattern)

    val date = LocalDate.parse(this, originalFormatter)
    return date.format(newFormatter)
}


fun String.stringToLocalDate(pattern: String = "dd/MM/yyyy"): LocalDate {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return LocalDate.parse(this, formatter)
}

fun String.capitalizeFirstLetter():String{

    return this[0].uppercase() + this.subSequence(1, this.length)
}
fun String.translateDay():String{
    var day = this
    when (this.lowercase()) {
        "monday" -> day = "Montag"
        "tuesday" -> day = "Dienstag"
        "wednesday" -> day = "Mittwoch"
        "thursday" -> day = "Donnerstag"
        "friday" -> day = "Freitag"
    }
    return day
}

fun String.translateListType():String{
    var type = this
    when (this.lowercase()) {
        ReportListType.BOOKMARKS.toString().lowercase() -> type = "Lesezeichen"
        ReportListType.TRASH.toString().lowercase() -> type = "Papierkorp"
    }
    return type
}





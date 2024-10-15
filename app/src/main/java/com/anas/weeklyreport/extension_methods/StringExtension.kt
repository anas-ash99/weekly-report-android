package com.anas.weeklyreport.extension_methods

import android.util.Log
import com.anas.weeklyreport.shared.ReportListType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

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

fun String.formatCreatedAt(pattern: String = "MMM d,yyyy"): String {
    return try {
        val originalDateTime = LocalDateTime.parse(this)
        val formatter = DateTimeFormatter.ofPattern(pattern)
        originalDateTime.format(formatter)
    }catch (e:Exception){
        Log.e("formatCreatedAt", e.message, e)
        ""
    }
}


fun String.calenderWeek():String{
     return try {
         val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
         val date = LocalDate.parse(this, formatter)

         val weekFields = WeekFields.of(Locale.getDefault())
         date.get(weekFields.weekOfWeekBasedYear()).toString()
     } catch (e:Exception){
         Log.e("get calender week", e.message, e)
         ""
     }
}
fun String.getYear():String{
     return try {
         val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
         val date = LocalDate.parse(this, formatter)
         date.year.toString()
     } catch (e:Exception){
         ""
     }
}

fun String.convertStringToDate(pattern: String):LocalDate{
     return try {
         val formatter = DateTimeFormatter.ofPattern(pattern)
         LocalDate.parse(this, formatter)
     } catch (e:Exception){
//         Log.e("get year from date", e.message, e)
         LocalDate.now()
     }
}




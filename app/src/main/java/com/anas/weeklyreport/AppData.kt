package com.anas.weeklyreport


import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.KeyboardType
import com.anas.weeklyreport.model.Description
import com.anas.weeklyreport.model.Report
import com.anas.weeklyreport.model.Weekday
import com.anas.weeklyreport.model.ui.ReportTextField
import com.anas.weeklyreport.shared.TextFieldName
import java.time.LocalDateTime
import java.util.UUID


object AppData {

    var allReports = mutableStateOf(arrayListOf<Report>())
    var testReports = generateReportDocuments(1)
    var appLanguage = ""
    const val sharedPreferencesLanguageKey = "app_language"


}





private fun generateReportDocuments(count: Int): List<Report> {
    return (1..count).map { index ->
        Report(
            id = UUID.randomUUID().toString(),
            name = "Report $index",
            year = "2024",
            calenderWeak = "CW${index % 52 + 1}",
            fromDate = "25/03/2024",
            toDate = "29/03/2024",
            reportNumber = "REP-" + String.format("%05d", index),
            weekdayDescription = generateWeekdayDescriptions(),
            createdAt = LocalDateTime.now().toString()
        )
    }
}

private fun generateWeekdayDescriptions(): List<Weekday> {
    val days = listOf("monday", "tuesday", "wednesday", "thursday", "friday")
    return days.map { day ->
        Weekday(day, generateDescriptions())
    }
}

private fun generateDescriptions(): ArrayList<Description> {
    return arrayListOf(
        Description("Softly alternate the background color of each card (e.g., very light gray for odd-indexed items). Be mindful of contrast for accessibility", "2.5"),
        Description("Consider adding a subtle drop shadow below each card to visually lift it from the background.", "3"),
        Description("Meeting", "1")
    )
}

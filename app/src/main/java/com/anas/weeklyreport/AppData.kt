package com.anas.weeklyreport


import com.anas.weeklyreport.model.Description
import com.anas.weeklyreport.model.Report
import com.anas.weeklyreport.model.User
import com.anas.weeklyreport.model.Weekday
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDateTime
import java.util.UUID


object AppData {
    var allReports = MutableStateFlow<ArrayList<Report>>(arrayListOf())
    var loggedInUser: User? = null
    var userToken:String? = null
    var testReports = generateReportDocuments(1)
    var appLanguage = ""
    const val sharedPreferencesLanguageKey = "app_language"
    const val privacyPolicyUrl = "https://anas-ash99.github.io/weekly-report-privacy-policy"
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
        Weekday(day = day, descriptions = generateDescriptions())
    }
}

private fun generateDescriptions(): ArrayList<Description> {
    return arrayListOf(
        Description(description = "Softly alternate the background color of each card (e.g., very light gray for odd-indexed items). Be mindful of contrast for accessibility", hours = "2.5"),
        Description(description = "Consider adding a subtle drop shadow below each card to visually lift it from the background.", hours = "3"),
        Description(description = "Meeting", hours = "1")
    )
}

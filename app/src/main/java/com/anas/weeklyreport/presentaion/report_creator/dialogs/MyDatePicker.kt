package com.anas.weeklyreport.presentaion.report_creator.dialogs

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalFocusManager
import com.anas.weeklyreport.domain.ReportCreatorScreenEvent
import com.anas.weeklyreport.domain.ReportCreatorScreenStates
import com.anas.weeklyreport.extension_methods.reformatDate
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePicker(
    state: ReportCreatorScreenStates,
    onEvent:(ReportCreatorScreenEvent) ->Unit,
){
    val focusManager = LocalFocusManager.current

    if (state.isDatePickerShown) {
        CalendarDialog(
            state = rememberUseCaseState(visible = true, true, onCloseRequest = {
                onEvent(ReportCreatorScreenEvent.OnDatePickerCloseRequest)
                focusManager.clearFocus()
            } ),
            config = CalendarConfig(
                yearSelection = true,
                style = CalendarStyle.MONTH,
            ),
            selection = CalendarSelection.Date(
                selectedDate = state.selectedDatePicker
            ) { newDate ->
                val text = newDate.toString().reformatDate()
                onEvent(ReportCreatorScreenEvent.OnDateSelection(text, ""))

            },
        )

    }
}
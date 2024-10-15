package com.anas.weeklyreport.presentaion.report_creator.dialogs

import android.util.Range
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.util.toRange
import com.anas.weeklyreport.screen_actions.ReportCreatorScreenEvent
import com.anas.weeklyreport.data.ReportCreatorScreenStates
import com.anas.weeklyreport.extension_methods.reformatDate
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePicker(
    state: ReportCreatorScreenStates,
    onEvent:(ReportCreatorScreenEvent) ->Unit,
    fromDate:String,
    toDate:String
){
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val selectedRange = remember {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        try {
            mutableStateOf(Range(LocalDate.parse(fromDate, formatter), LocalDate.parse(toDate, formatter)))
        }catch (e:Exception){
            mutableStateOf(Range(LocalDate.now(), LocalDate.now()))
        }

    }
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
            selection = CalendarSelection.Period(
                selectedRange = selectedRange.value
            ) { startDate, endDate  ->
                selectedRange.value = Range(startDate, endDate)
                onEvent(ReportCreatorScreenEvent.OnDateSelection(startDate.toString().reformatDate(), endDate.toString().reformatDate()))
            },
        )

    }
}








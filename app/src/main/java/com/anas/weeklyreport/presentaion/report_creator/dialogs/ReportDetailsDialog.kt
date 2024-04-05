package com.anas.weeklyreport.presentaion.report_creator.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anas.weeklyreport.AppData
import com.anas.weeklyreport.R
import com.anas.weeklyreport.screen_actions.ReportCreatorScreenEvent
import com.anas.weeklyreport.data.ReportCreatorScreenStates
import com.anas.weeklyreport.extension_methods.capitalizeFirstLetter
import com.anas.weeklyreport.extension_methods.translateDay
import com.anas.weeklyreport.model.Weekday
import com.anas.weeklyreport.shared.AppColors

@Composable
fun ReportPreviewScreen(weekdayDescription: List<Weekday>) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier
        .fillMaxWidth(0.9f)) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
            verticalArrangement= Arrangement.Center,
            horizontalAlignment= Alignment.Start,
        ) {

            weekdayDescription.forEach { weekday ->
                val dayText = if (AppData.appLanguage == "de") weekday.day.translateDay().capitalizeFirstLetter() else weekday.day.capitalizeFirstLetter()
                Text(text = "$dayText:",  fontWeight = FontWeight.Bold, fontSize = 20.sp)
                weekday.descriptions.forEach { desc ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,

                        ) {
                        Text(
                            text ="- ",
                        )
                        Text(
                            text ="${desc.description} ",
                            modifier = Modifier.weight(1f)
                        )
                        Text(text = "${desc.hours} ${stringResource(id = R.string.hours).lowercase()}", modifier = Modifier.padding(start = 5.dp))
                    }

                }
            }
        }
    }
}

@Composable
fun ReportDetailsDialog(weekdayDescription: List<Weekday>, state:ReportCreatorScreenStates, onEvent:(ReportCreatorScreenEvent) ->Unit){
    if (state.isPreviewDialogShown){
        AlertDialog(
            modifier = Modifier.padding(vertical = 15.dp),
            title = {
                Text(text = stringResource(id = R.string.preview), fontWeight = FontWeight.Medium, fontSize = 25.sp,  modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp), textAlign = TextAlign.Center)
            },
            onDismissRequest = {
                onEvent(ReportCreatorScreenEvent.OnPreviewDialogRequest(false))
            },
            dismissButton = {
                Button(
                    onClick = {
                        onEvent(ReportCreatorScreenEvent.OnPreviewDialogRequest(false))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.CANCEL_BUTTON_COLOR.color),
                ) {
                    Text(stringResource(id = R.string.cancel))
                }
            },
            confirmButton = {
                Button(
                    onClick = {

                        onEvent(ReportCreatorScreenEvent.OnPreviewDialogConfirmClick)
                        onEvent(ReportCreatorScreenEvent.OnPreviewDialogRequest(false))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.APP_MAIN_COLOR.color),
                ) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            },
            text = {
                ReportPreviewScreen(weekdayDescription )
            }
        )
    }
}

@Preview
@Composable
fun ReportPreviewScreenPreview() {
    ReportDetailsDialog(AppData.testReports[0].weekdayDescription, ReportCreatorScreenStates(isPreviewDialogShown = true), {})
}
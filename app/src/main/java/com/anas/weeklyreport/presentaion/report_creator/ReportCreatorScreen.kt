package com.anas.weeklyreport.presentaion.report_creator

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.anas.weeklyreport.R
import com.anas.weeklyreport.domain.ReportCreatorScreenEvent
import com.anas.weeklyreport.presentaion.LoadingSpinner
import com.anas.weeklyreport.presentaion.report_creator.dialogs.AddDescriptionDialog
import com.anas.weeklyreport.presentaion.report_creator.dialogs.AiAssistantDialog
import com.anas.weeklyreport.presentaion.report_creator.dialogs.MyDatePicker
import com.anas.weeklyreport.presentaion.report_creator.dialogs.ReportDetailsDialog
import com.anas.weeklyreport.shared.AppColors
import com.anas.weeklyreport.viewmodels.ReportCreatorViewmodel

@SuppressLint("FrequentlyChangedStateReadInComposition", "UnrememberedMutableState")
@Composable
fun ReportCreatorScreen(navController: NavHostController?, reportId:String) {

    val viewmodel:ReportCreatorViewmodel = hiltViewModel()
    val context = LocalContext.current
    viewmodel.init(reportId)
    val state = viewmodel.state.collectAsState()
    val document by viewmodel.report.collectAsState()

    LaunchedEffect(state.value.screen){ // launchedEffect for handling navigation
        if (state.value.screen.isNotBlank()){
            navController?.popBackStack()

        }
    }

    LaunchedEffect(state.value.toastMessage){
        if (state.value.toastMessage.isNotBlank()){
            Toast.makeText(context, state.value.toastMessage, Toast.LENGTH_SHORT).show()
           viewmodel.restToastMessage()
        }
    }


    Surface (modifier = Modifier.fillMaxSize()) {

        ReportDetailsDialog(weekdayDescription = state.value.previewWeekDays, state = state.value, onEvent = viewmodel::onEvent)
        AiAssistantDialog(state = state.value, onEvent = viewmodel::onEvent)
        Scaffold (
            topBar = {
                ScreenAppBar(navController = navController, onEvent =  viewmodel::onEvent)
            },
            floatingActionButton = {
                val saveButtonEnabled:  Boolean = document.name.isNotBlank()  && document.reportNumber.isNotBlank()
                Button(
                    onClick = { viewmodel.onEvent(ReportCreatorScreenEvent.OnReportSaveClick) },
                    colors = ButtonDefaults.buttonColors(AppColors.APP_MAIN_COLOR.color),
                    enabled = saveButtonEnabled ,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(42.dp)
                ) {
                    Text(text = stringResource(id = R.string.save), color = Color.White, fontSize = 18.sp)
                }
            },
            floatingActionButtonPosition = FabPosition.Center // Place in the center
        ){ innerPadding  ->
            Column(modifier = Modifier
                .padding(innerPadding)
                .padding(start = 16.dp, end = 16.dp, bottom = 10.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()))
            {
                CustomStyledTextField(text = document.name, onEvent = viewmodel::onEvent, label = stringResource(id = R.string.report_name), keyboardType = KeyboardType.Text)
                CustomStyledTextField(text = document.reportNumber,onEvent = viewmodel::onEvent, label = stringResource(id = R.string.report_number))
                CustomStyledTextField(text = document.year,onEvent = viewmodel::onEvent, label = stringResource(id = R.string.year))
                CustomStyledTextField(text = document.calenderWeak,onEvent = viewmodel::onEvent, label = stringResource(id = R.string.week))
                DateTextField(
                    label = stringResource(id = R.string.from_date),
                    state = state.value,
                    onEvent = viewmodel::onEvent,
                    text = document.fromDate
                )

                DateTextField(
                    label = stringResource(id = R.string.to_date),
                    text = document.toDate,
                    state = state.value,
                    onEvent = viewmodel::onEvent
                )

                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement= Arrangement.SpaceBetween,
                    verticalAlignment= Alignment.CenterVertically,
                ){
                    Text(stringResource(id = R.string.weekday_descriptions), style = MaterialTheme.typography.headlineSmall)
                    TextButton(onClick = {
                        state.value.previewWeekDays = viewmodel.report.value.weekdayDescription
                        viewmodel.onEvent(ReportCreatorScreenEvent.OnPreviewDialogRequest(true))
                    }) {
                        Text(
                            modifier = Modifier.padding(top = 6.dp),
                            text = stringResource(id = R.string.preview),
                            fontSize = 16.sp,
                            color = Color(0xFF326d8b)
                        )
                    }
                }
                document.weekdayDescription.forEach { item ->
                    WeekdayItem(
                        day= item.day,
                        data = item,
                        state = state.value,
                        onEvent = viewmodel::onEvent
                    )
                }
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp))
                AddDescriptionDialog(
                    onEvent = viewmodel::onEvent,
                    state = state.value,
                )
                MyDatePicker(state = state.value, onEvent = viewmodel::onEvent)
            }
        }
        LoadingSpinner(isVisible = state.value.screenLoading)
    }
}

@Preview
@Composable
fun ReportCreatorScreenPreview() {
    ReportCreatorScreen(null, "empty")
}




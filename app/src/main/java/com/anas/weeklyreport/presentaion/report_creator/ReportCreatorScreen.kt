package com.anas.weeklyreport.presentaion.report_creator



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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.anas.weeklyreport.R
import com.anas.weeklyreport.model.ui.ReportTextField
import com.anas.weeklyreport.screen_actions.ReportCreatorScreenEvent
import com.anas.weeklyreport.presentaion.LoadingSpinner
import com.anas.weeklyreport.presentaion.MyNotificationMessage
import com.anas.weeklyreport.presentaion.report_creator.dialogs.AddDescriptionDialog
import com.anas.weeklyreport.presentaion.report_creator.dialogs.AiAssistantDialog
import com.anas.weeklyreport.presentaion.report_creator.dialogs.MyDatePicker
import com.anas.weeklyreport.presentaion.report_creator.dialogs.ReportDetailsDialog
import com.anas.weeklyreport.shared.AppColors
import com.anas.weeklyreport.shared.TextFieldName
import com.anas.weeklyreport.viewmodels.ReportCreatorViewmodel



@Composable
fun ReportCreatorScreen(navController: NavHostController?, reportId:String) {

    val viewmodel:ReportCreatorViewmodel = hiltViewModel()
    viewmodel.init(reportId)
    val state by viewmodel.state.collectAsState()
    val report by viewmodel.report.collectAsState()
    val textFields = initTextFields(
        name = report.name,
        reportNumber = report.reportNumber,
        year = report.year,
        calenderWeak = report.calenderWeak,
        fromDate = report.fromDate,
        toDate = report.toDate
    )


    LaunchedEffect(state.screen){ // launchedEffect for handling navigation
        if (state.screen.isNotBlank()){
            navController?.popBackStack()
        }
    }

//    LaunchedEffect(state.toastMessage){
//        if (state.toastMessage != 0){
//            Toast.makeText(context, stringResource(id = state.toastMessage), Toast.LENGTH_SHORT).show()
//           viewmodel.restToastMessage()
//        }
//    }


    Surface (modifier = Modifier.fillMaxSize()) {

        ReportDetailsDialog(weekdayDescription = state.previewWeekDays, state = state, onEvent = viewmodel::onEvent)
        AiAssistantDialog(state = state, onEvent = viewmodel::onEvent)
        Scaffold (
            topBar = {
                ScreenAppBar(navController = navController, onEvent =  viewmodel::onEvent)
            },
            floatingActionButton = {
                val saveButtonEnabled = report.reportNumber.isNotBlank() && report.calenderWeak.isNotBlank()
                        && report.fromDate.isNotBlank() && report.toDate.isNotBlank()
                Button(
                    onClick = { viewmodel.onEvent(ReportCreatorScreenEvent.OnReportSaveClick) },
                    colors = ButtonDefaults.buttonColors(AppColors.APP_MAIN_COLOR),
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
                textFields.forEach { item ->
                    if (!item.isDate){
                        CustomStyledTextField(text = item.value, onEvent = viewmodel::onEvent, label = stringResource(id = item.label) , keyboardType = item.type, name = item.name)
                    }else{
                        DateTextField(label = stringResource(id = item.label), text = item.value, name = item.name, onEvent = viewmodel::onEvent)
                    }
                }

                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement= Arrangement.SpaceBetween,
                    verticalAlignment= Alignment.CenterVertically,
                ){
                    Text(stringResource(id = R.string.weekday_descriptions), style = MaterialTheme.typography.headlineSmall)
                    TextButton(onClick = {
                        state.previewWeekDays = viewmodel.report.value.weekdayDescription
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
                report.weekdayDescription.forEach { item ->
                    WeekdayItem(
                        day= item.day,
                        data = item,
                        state = state,
                        onEvent = viewmodel::onEvent
                    )
                }
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp))
                AddDescriptionDialog(
                    onEvent = viewmodel::onEvent,
                    state = state,
                )
                MyDatePicker(state = state, onEvent = viewmodel::onEvent, report.fromDate, report.toDate)
            }
        }


        LoadingSpinner(isVisible = state.screenLoading)

    }
    MyNotificationMessage(
        message = if (state.notificationMessage != 0 ) stringResource(id = state.notificationMessage) else "",
        color = state.notificationColor,
        isVisible = state.isNotificationMessageShown){
            viewmodel.onEvent(ReportCreatorScreenEvent.RequestNotificationMessage(false))
    }
}
fun initTextFields (name:String, reportNumber:String, year:String, calenderWeak:String, fromDate:String, toDate:String):List<ReportTextField> {
    return listOf(
//        ReportTextField(
//            value = name,
//            name = TextFieldName.REPORT_NAME,
//            label = R.string.report_name,
//            type = KeyboardType.Text,
//        ),
        ReportTextField(
            value = reportNumber,
            name = TextFieldName.REPORT_NUMBER,
            label = R.string.report_number,
        ),
        ReportTextField(
            value = "$fromDate - $toDate",
            name = TextFieldName.FROM_DATE,
            label = R.string.date_range,
            type = KeyboardType.Text,
            isDate = true
        ),
        ReportTextField(
            value = calenderWeak,
            name = TextFieldName.WEEK,
            label = R.string.week,
        ),
        ReportTextField(
            value = year,
            name = TextFieldName.YEAR,
            label = R.string.year,
        )

        )
}

@Preview
@Composable
fun ReportCreatorScreenPreview() {
    ReportCreatorScreen(null, "empty")
}




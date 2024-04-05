package com.anas.weeklyreport.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anas.weeklyreport.shared.AppScreen
import com.anas.weeklyreport.AppData.allReports
import com.anas.weeklyreport.data.DataState
import com.anas.weeklyreport.screen_actions.ReportCreatorScreenEvent
import com.anas.weeklyreport.data.ReportCreatorScreenStates
import com.anas.weeklyreport.data.repository.MyServerRepo
import com.anas.weeklyreport.extension_methods.stringToLocalDate
import com.anas.weeklyreport.model.Description
import com.anas.weeklyreport.model.Report
import com.anas.weeklyreport.shared.TextFieldName.FROM_DATE
import com.anas.weeklyreport.shared.TextFieldName.REPORT_NAME
import com.anas.weeklyreport.shared.TextFieldName.REPORT_NUMBER
import com.anas.weeklyreport.shared.TextFieldName.TO_DATE
import com.anas.weeklyreport.shared.TextFieldName.WEEK
import com.anas.weeklyreport.shared.TextFieldName.YEAR
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject


@HiltViewModel
class ReportCreatorViewmodel @Inject constructor(
    private val myServerRepo: MyServerRepo
):ViewModel() {

    val report = MutableStateFlow(Report())
    val state = MutableStateFlow(ReportCreatorScreenStates())
    private var isPageInitialized = false


    fun init(reportId:String){
        if (reportId != "empty" && !isPageInitialized){
            report.value = allReports.value.filter { it.id == reportId }[0]
            isPageInitialized = true
        }
    }

    fun onEvent(event: ReportCreatorScreenEvent){
        when(event){
            is ReportCreatorScreenEvent.OnAddDescriptionClick -> {
                state.update { state -> state.copy(
                    dialogDescriptionItem = Description(),
                    isDialogTypeAdd = true,
                    currentWeekDayItem = event.day,
                    isAddDescriptionDialogShown = true,
                ) }
            }
            is ReportCreatorScreenEvent.OnEditDescriptionClick -> {
                state.update { state -> state.copy(
                    isDialogTypeAdd = false,
                    descriptionItemIndex = event.index,
                    currentWeekDayItem = event.day,
                    isAddDescriptionDialogShown = true,
                    dialogDescriptionItem = event.description,
                ) }
            }
            ReportCreatorScreenEvent.OnReportSaveClick -> {
                if (report.value.id.isBlank()){ // add new report
                    report.value.createdAt = LocalDateTime.now().toString()
                    saveReportRemotely("save")
                }else{ // edit an existing report
                    saveReportRemotely("update")
                }

            }
            is ReportCreatorScreenEvent.OnTextFieldValueChange -> handleTextValueChange(event.fieldName, event.value )

            ReportCreatorScreenEvent.OnAddDialogDismiss -> {
                state.update { state -> state.copy(
                    isAddDescriptionDialogShown = false,
                ) }
            }

            ReportCreatorScreenEvent.OnSaveDescriptionClick -> updateDayDescription()


            is ReportCreatorScreenEvent.OnDescriptionDialogValueChange -> {
                state.update { state -> state.copy(
                    dialogDescriptionItem = Description(description = event.value, hours = state.dialogDescriptionItem.hours),
                ) }


            }
            is ReportCreatorScreenEvent.OnHoursDialogValueChange -> {
                state.update { state -> state.copy(
                    dialogDescriptionItem = Description(description = state.dialogDescriptionItem.description, hours = event.value ),

                ) }
            }
            is ReportCreatorScreenEvent.RequestDatePicker -> {
                state.update { state -> state.copy(
                    isDatePickerShown = event.showDatePicker,
                    currentDatePickerField = event.field
                ) }
                 assignDateToDatePicker(event.showDatePicker)
            }

            is ReportCreatorScreenEvent.OnDateSelection -> {
                when(state.value.currentDatePickerField){
                    FROM_DATE->{
                       report.update { doc ->
                           doc.copy(
                               fromDate = event.date
                           )
                       }
                    }
                    TO_DATE -> {
                        report.update { doc ->
                            doc.copy(
                                toDate = event.date
                            )
                        }

                    }
                }
            }

            ReportCreatorScreenEvent.OnDatePickerCloseRequest -> {
                state.update { state -> state.copy(
                   isDatePickerShown = false
                ) }
            }

            is ReportCreatorScreenEvent.OnDateTextFieldValueChange -> {
                state.update { state -> state.copy(
                    promptText = event.value
                ) }
            }
            is ReportCreatorScreenEvent.OnAiDialogRequest -> {
                state.update { state -> state.copy(
                    promptText = "",
                    isAiDialogShown = event.isShown
                ) }
            }
            ReportCreatorScreenEvent.OnGeneraAiResponseClick -> generateAiResponse()
            is ReportCreatorScreenEvent.OnPromptTextChange -> {
                state.update { state -> state.copy(
                    promptText = event.value
                ) }
            }

            is ReportCreatorScreenEvent.OnPreviewDialogRequest -> {
                state.update { state -> state.copy(
                    previewWeekDays = if (event.isShown) report.value.weekdayDescription else arrayListOf(),
                    isPreviewDialogShown = event.isShown
                ) }
            }

            ReportCreatorScreenEvent.OnPreviewDialogConfirmClick -> {
                report.update { body -> body.copy(
                    weekdayDescription = state.value.previewWeekDays
                ) }

            }
            is ReportCreatorScreenEvent.RequestNotificationMessage ->  state.update { state -> state.copy(isNotificationMessageShown = event.isShown) }
            is ReportCreatorScreenEvent.RequestContextMenu -> state.update { state -> state.copy(isContextMenuShown = event.isShown) }
        }
    }

    private fun updateDayDescription() {
       val description = Description(state.value.dialogDescriptionItem.description, state.value.dialogDescriptionItem.hours)
        report.value.weekdayDescription.forEach { des->
            if (des.day == state.value.currentWeekDayItem){
                if (state.value.isDialogTypeAdd){
                    des.descriptions.add(description)
                }else{
                    des.descriptions[state.value.descriptionItemIndex] = description
                }
            }
        }
        state.update { state ->
            state.copy(
                isAddDescriptionDialogShown = false,
                dialogDescriptionItem = Description(),
                weekDayListTrigger = state.weekDayListTrigger + 1
            )
        }
    }

    private fun assignDateToDatePicker(showDatePicker: Boolean) {
        if (showDatePicker){
            when(state.value.currentDatePickerField){
                FROM_DATE->{
                    if (report.value.fromDate.isNotBlank()){
                        state.update { state -> state.copy(
                            selectedDatePicker = report.value.fromDate.stringToLocalDate()
                        ) }
                    }
                }
                TO_DATE -> {
                    if (report.value.toDate.isNotBlank()){
                        state.update { state -> state.copy(
                            selectedDatePicker = report.value.toDate.stringToLocalDate()
                        ) }
                    }
                }
            }
        }
    }


    private fun handleTextValueChange(field: String, value: String){
        when(field){
            REPORT_NAME ->{
                report.update { doc ->
                    doc.copy(
                        name = value
                    )
                }
            }
            REPORT_NUMBER->{
                report.update { doc ->
                    doc.copy(
                        reportNumber = value
                    )
                }
            }
            YEAR -> {
                report.update { doc ->
                    doc.copy(
                        year = value
                    )
                }
            }
            WEEK ->{
                report.update { doc ->
                    doc.copy(
                        calenderWeak = value
                    )
                }
            }
        }
    }
    private fun generateAiResponse() {

        viewModelScope.launch {
            myServerRepo.getOpenAiChatResponse(state.value.promptText).onEach { stateFlow ->
                when(stateFlow){
                    is DataState.Success ->{
                        val aiResponse = stateFlow.data
                        state.update { body ->
                            body.copy(
                                previewWeekDays = aiResponse.weekdayDescription,
                                isPreviewDialogShown = true,
                                screenLoading = false
                            )
                        }

                    }
                    is DataState.Error -> {
                        state.update { body ->
                            body.copy(
                                toastMessage = stateFlow.exception.message!!,
                                screenLoading = false
                            )
                        }
                    }
                    DataState.Loading -> {
                        state.update { body -> body.copy(
                            screenLoading = true
                        ) }
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun saveReportRemotely(type:String) {
        viewModelScope.launch {
            myServerRepo.saveReport(report.value).onEach { stateFlow ->
                when(stateFlow){
                    is DataState.Success -> {
                        var toastMessage  =""
                        when(type){
                            "save" ->{
                                toastMessage = "Report added successfully"
                                allReports.value.add(0 , stateFlow.data)
                                state.value.screen = AppScreen.HOME_SCREEN.toString() // only go back when
                            }
                            "update" ->{
                                if(updateItem(report.value)){
                                    toastMessage = "Report added updated"
                                    state.value.screen = AppScreen.HOME_SCREEN.toString() // only go back when
                                }else{
                                    toastMessage = "Unknown error occurred"
                                }
                            }
                        }

                        state.update { body -> body.copy(
                            toastMessage = toastMessage,
                            screenLoading = false,
                        ) }
                    }
                    is DataState.Error -> {
                        state.update { body ->
                            body.copy(
                                toastMessage = "Failed to save report, please try again later!",
                                screenLoading = false
                            )
                        }
                    }
                    DataState.Loading -> {
                     state.update { body -> body.copy(screenLoading = true) }
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun restToastMessage() {
        state.value.toastMessage = ""
    }

    private fun updateItem(report:Report):Boolean{
        var isUpdated = false
        try {
            allReports.value.forEachIndexed{index, doc->
                if (doc.id == report.id){
                    allReports.value[index] = report
                    isUpdated = true
                }
            }

        }catch (e:Exception){
            isUpdated = false
            Log.e("update item", e.message, e)
        }
        return isUpdated
    }

}



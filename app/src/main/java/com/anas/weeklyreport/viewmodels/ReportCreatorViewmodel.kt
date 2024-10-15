package com.anas.weeklyreport.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anas.weeklyreport.shared.AppScreen
import com.anas.weeklyreport.AppData.allReports
import com.anas.weeklyreport.R
import com.anas.weeklyreport.data.Result
import com.anas.weeklyreport.screen_actions.ReportCreatorScreenEvent
import com.anas.weeklyreport.data.ReportCreatorScreenStates
import com.anas.weeklyreport.data.repository.ReportRepository
import com.anas.weeklyreport.extension_methods.calenderWeek
import com.anas.weeklyreport.extension_methods.stringToLocalDate
import com.anas.weeklyreport.model.Description
import com.anas.weeklyreport.model.Report
import com.anas.weeklyreport.shared.AppColors
import com.anas.weeklyreport.shared.NetworkException
import com.anas.weeklyreport.shared.TextFieldName.FROM_DATE
import com.anas.weeklyreport.shared.TextFieldName.REPORT_NAME
import com.anas.weeklyreport.shared.TextFieldName.REPORT_NUMBER
import com.anas.weeklyreport.shared.TextFieldName.TO_DATE
import com.anas.weeklyreport.shared.TextFieldName.WEEK
import com.anas.weeklyreport.shared.TextFieldName.YEAR
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class ReportCreatorViewmodel @Inject constructor(
    private val reportRepository: ReportRepository
):ViewModel() {

    val report = MutableStateFlow(Report())
    val state = MutableStateFlow(ReportCreatorScreenStates())
    private var isPageInitialized = false

    fun init(reportId:String){
        if (reportId != "empty" && !isPageInitialized){ // retrieve report from report list
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
                    saveReport()
                }else{ //update an existing report
                    updateReport()
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
                ) }
            }

            is ReportCreatorScreenEvent.OnDateSelection -> {
                report.update { doc ->
                    doc.copy(
                        fromDate = event.fromDate,
                        toDate = event.toDate,
                        calenderWeak = event.fromDate.calenderWeek()
                    )
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
                    promptText = if (event.isShown) "" else state.promptText,
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
            is ReportCreatorScreenEvent.RequestNotificationMessage -> {
                state.update { state ->
                    state.copy(
                        isNotificationMessageShown = event.isShown,
                    ) }
            }
            is ReportCreatorScreenEvent.RequestContextMenu -> state.update { state -> state.copy(isContextMenuShown = event.isShown) }
        }
    }

    private fun updateDayDescription() {
       val description = Description(description = state.value.dialogDescriptionItem.description, hours = state.value.dialogDescriptionItem.hours)
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
            reportRepository.getOpenAiChatResponse(state.value.promptText).onEach { stateFlow ->
                when(stateFlow){
                    is Result.Success ->{
                        val aiResponse = stateFlow.data
                        state.update { body ->
                            body.copy(
                                previewWeekDays = aiResponse.weekdayDescription,
                                notificationColor = AppColors.NotificationSuccessColor,
                                isPreviewDialogShown = true,
                                screenLoading = false
                            )
                        }
                    }
                    is Result.Error -> {
                        state.update { body ->
                            body.copy(
                                notificationMessage = R.string.aiReportGeneratorErrorMessage,
                                notificationColor = AppColors.NotificationErrorColor,
                                isNotificationMessageShown = true,
                                screenLoading = false
                            )
                        }

                        delay(1500)
                        state.update { body ->
                            body.copy(
                                isAiDialogShown = true
                            )
                        }
                    }
                    Result.Loading -> {
                        state.update { body -> body.copy(
                            screenLoading = true
                        ) }
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun saveReport() {
        viewModelScope.launch {
            reportRepository.createReport(report.value).onEach { stateFlow ->
                when(stateFlow){
                    is Result.Success -> {
                        state.update { body -> body.copy(
                            notificationMessage = R.string.repor_saved_message,
                            notificationColor = AppColors.NotificationSuccessColor,
                            isNotificationMessageShown = true,
                            screenLoading = false,
                        ) }

                        delay(1100)
                        state.update { body -> body.copy(
                            notificationColor = Color.Transparent,
                            screen = AppScreen.HOME_SCREEN.toString(),
                        ) }
                    }
                    is Result.Error -> {
                        var message = 0
                         when(stateFlow.exception){
                            is NetworkException -> {
                                message = R.string.having_trouble_connecting_error_message
                                CoroutineScope(Dispatchers.IO).launch {
                                    delay(1000)
                                    state.update { body ->
                                        body.copy(
                                            notificationColor = Color.Transparent,
                                            screen = AppScreen.HOME_SCREEN.toString()
                                        )
                                    }
                                }
                            }
                            else -> {
                               message = R.string.unknownErrorMessage
                            }
                        }
                        state.update { body ->
                            body.copy(
                                notificationMessage = message,
                                notificationColor = AppColors.NotificationErrorColor,
                                isNotificationMessageShown = true,
                                screenLoading = false
                            )
                        }

                    }
                    Result.Loading -> {
                     state.update { body -> body.copy(screenLoading = true) }
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun updateReport() {
        viewModelScope.launch {
            reportRepository.updateReport(report.value).onEach { stateFlow ->
                when(stateFlow){
                    is Result.Success -> {
                        state.update { body -> body.copy(
                            notificationMessage = R.string.repor_saved_message,
                            notificationColor = AppColors.NotificationSuccessColor,
                            isNotificationMessageShown = true,
                            screenLoading = false,
                        ) }
                        // delay so the user can see the notification message
                        delay(1000)
                        state.update { body -> body.copy(
                            notificationColor = Color.Transparent,
                            screen = AppScreen.HOME_SCREEN.toString(),
                        ) }
                    }
                    is Result.Error -> {
                        var message = 0
                        when(stateFlow.exception) {
                            is NetworkException -> {
                                message = R.string.having_trouble_connecting_error_message

                                CoroutineScope(Dispatchers.IO).launch {
                                    // Code to be executed asynchronously
                                    delay(1500)
                                    state.update { body ->
                                        body.copy(
                                            notificationColor = Color.Transparent,
                                            screen = AppScreen.HOME_SCREEN.toString()
                                        )
                                    }
                                }
                            }
                            else -> {
                                message = R.string.unknownErrorMessage
                            }
                        }
                        state.update { body ->
                            body.copy(
                                notificationMessage = message,
                                notificationColor = AppColors.NotificationErrorColor,
                                isNotificationMessageShown = true,
                                screenLoading = false
                            )
                        }
                    }
                    Result.Loading -> {
                        state.update { body -> body.copy(screenLoading = true) }
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun restToastMessage() {
        state.value.toastMessage = 0
    }

}



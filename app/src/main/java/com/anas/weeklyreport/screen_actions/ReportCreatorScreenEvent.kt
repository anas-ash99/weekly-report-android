package com.anas.weeklyreport.screen_actions

import android.content.Context
import com.anas.weeklyreport.model.Description

sealed interface ReportCreatorScreenEvent{

    data class OnTextFieldValueChange(val value:String, val fieldName:String): ReportCreatorScreenEvent
    object OnReportSaveClick: ReportCreatorScreenEvent
    data class OnAddDescriptionClick(val day: String): ReportCreatorScreenEvent
    data class OnEditDescriptionClick(val description: Description, val day:String, val index:Int): ReportCreatorScreenEvent
    object OnSaveDescriptionClick: ReportCreatorScreenEvent
    data class OnDescriptionDialogValueChange(val value:String): ReportCreatorScreenEvent
    data class OnHoursDialogValueChange(val value:String): ReportCreatorScreenEvent
    data class RequestDatePicker(val field:String, val showDatePicker: Boolean): ReportCreatorScreenEvent
    data class OnDateSelection(val date:String, val field:String): ReportCreatorScreenEvent
    data class OnDateTextFieldValueChange(val value:String, val field:String): ReportCreatorScreenEvent
    object OnAddDialogDismiss: ReportCreatorScreenEvent
    object OnDatePickerCloseRequest: ReportCreatorScreenEvent
    object OnGeneraAiResponseClick: ReportCreatorScreenEvent
    data class OnPromptTextChange(val value: String): ReportCreatorScreenEvent
    data class OnAiDialogRequest(val isShown:Boolean): ReportCreatorScreenEvent
    data class OnPreviewDialogRequest(val isShown:Boolean): ReportCreatorScreenEvent
    object OnPreviewDialogConfirmClick: ReportCreatorScreenEvent
    data class RequestNotificationMessage(val isShown: Boolean): ReportCreatorScreenEvent
    data class RequestContextMenu(val isShown: Boolean): ReportCreatorScreenEvent




}
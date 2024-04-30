package com.anas.weeklyreport.screen_actions

sealed interface SettingsScreenEvent{
    object OnLanguageClick:SettingsScreenEvent
    object OnProfileClick:SettingsScreenEvent
    data class RequestBottomSheet(val isShown:Boolean):SettingsScreenEvent
    data class RequestDeleteDialog(val isShown:Boolean):SettingsScreenEvent
    data class RequestNotificationMessage(val isShown:Boolean):SettingsScreenEvent
    data class OnLanguageSelection(val language:String):SettingsScreenEvent

    object OnDeleteClick:SettingsScreenEvent
    object OnConfirmAccountDelete:SettingsScreenEvent
    object OnPrivacyPolicyClick:SettingsScreenEvent
    object OnFeedbackClick:SettingsScreenEvent
}
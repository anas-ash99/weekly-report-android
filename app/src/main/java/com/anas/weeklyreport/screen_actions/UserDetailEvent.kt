package com.anas.weeklyreport.screen_actions;

sealed interface UserDetailEvent {
    data class OnTextFieldValueChange(val value:String, val label:Int):UserDetailEvent
    object OnSaveClick:UserDetailEvent
    data class RequestNotificationMessage(val isShown:Boolean):UserDetailEvent
}

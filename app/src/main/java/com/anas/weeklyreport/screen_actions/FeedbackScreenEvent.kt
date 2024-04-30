package com.anas.weeklyreport.screen_actions

sealed interface FeedbackScreenEvent{
    data class OnEmailValueChange(val value:String):FeedbackScreenEvent
    data class OnMessageValueChange(val value:String):FeedbackScreenEvent
    data class RequestNotificationMessage(val isShown:Boolean):FeedbackScreenEvent
    data class SelectEmoji(val emoji: String):FeedbackScreenEvent
   object OnSaveClick:FeedbackScreenEvent

}
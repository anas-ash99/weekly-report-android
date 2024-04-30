package com.anas.weeklyreport.data

import androidx.compose.ui.graphics.Color
import com.anas.weeklyreport.AppData
import com.anas.weeklyreport.screen_actions.SettingsScreenEvent

data class FeedbackScreenState(
    val email:String = AppData.loggedInUser!!.email,
    val message:String = "",
    val selectedEmoji:String = "",
    val screenLoading:Boolean = false,
    val notificationMessage:Int = 0,
    val notificationMessageColor: Color = Color.Transparent,
    val isNotificationMessageShown:Boolean = false,
    val goBack:Boolean = false,
)

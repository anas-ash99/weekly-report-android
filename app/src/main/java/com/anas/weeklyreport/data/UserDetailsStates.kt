package com.anas.weeklyreport.data

import androidx.compose.ui.graphics.Color
import com.anas.weeklyreport.R
import com.anas.weeklyreport.shared.AppColors

data class UserDetailsStates(
    val nameTextField:String ="",
    val companyField:String ="",
    val departmentField:String ="",
    val emailTextField:String ="",
    val isNotificationMessageShown:Boolean = false,
    val notificationMessage:Int = R.string.unknownErrorMessage,
    val notificationMessageColor:Color = AppColors.NotificationSuccessColor,
    var screen:String = "",
    val screenLoading:Boolean = false

)

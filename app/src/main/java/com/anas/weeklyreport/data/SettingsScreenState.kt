package com.anas.weeklyreport.data

import androidx.compose.ui.graphics.Color
import com.anas.weeklyreport.AppData
import com.anas.weeklyreport.shared.BottomSheetBodyType

data class SettingsScreenState(
    var isBottomSheetShown:Boolean = false,
    val bottomSheetType:BottomSheetBodyType = BottomSheetBodyType.EMPTY,
    val appLanguage:String = AppData.appLanguage,
    val recompositionTrigger:Int = 0,
    var screen:String = "",
    val isDeleteConfirmationDialogShown:Boolean = false,
    val screenLoading:Boolean = false,
    val notificationMessage:Int = 0,
    val isNotificationMessageShown:Boolean= false,
    val notificationMessageColor:Color = Color.Transparent
)

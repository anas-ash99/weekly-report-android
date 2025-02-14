package com.anas.weeklyreport.data

import androidx.compose.ui.graphics.Color
import com.anas.weeklyreport.model.Description
import com.anas.weeklyreport.model.Weekday
import com.anas.weeklyreport.shared.AppColors
import java.time.LocalDate

data class ReportCreatorScreenStates(
    var isDialogTypeAdd:Boolean = true,
    var isAddDescriptionDialogShown:Boolean = false,
    var isDatePickerShown:Boolean = false,
    var descriptionItemIndex:Int = 0,
    var weekDayListTrigger:Int = 0,
    var currentWeekDayItem:String = "Monday",
    var dialogDescriptionItem:Description = Description(),
    var selectedDatePicker: LocalDate = LocalDate.now(),
    var previewWeekDays:List<Weekday> = arrayListOf(),
    var notificationColor:Color = Color.Transparent,
    var toastMessage:Int = 0,
    var screenLoading:Boolean = false,
    var screen:String = "",
    var isAiDialogShown:Boolean = false,
    var promptText :String = "",
    var isPreviewDialogShown:Boolean = false,
    val isNotificationMessageShown: Boolean = false,
    val isContextMenuShown: Boolean = false,
    val notificationMessage:Int = 0,
)


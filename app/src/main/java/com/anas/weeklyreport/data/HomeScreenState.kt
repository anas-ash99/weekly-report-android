package com.anas.weeklyreport.data

import android.net.Uri
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.graphics.Color
import com.anas.weeklyreport.AppData
import com.anas.weeklyreport.model.Report
import com.anas.weeklyreport.model.User
import com.anas.weeklyreport.shared.AppColors
import com.anas.weeklyreport.shared.BottomSheetBodyType

data class HomeScreenState (
    val reports:MutableList<Report> = mutableListOf(),
    val loggedInUser: User = User(),
    var documentUri: Uri? = null,
    var isBottomSheetShown:Boolean = false,
    var screen:String = "",
    var screenLoading:Boolean = false,
    var itemsLoading:Boolean = true,
    var notificationMessage:Int = 0,
    var notificationColor: Color = AppColors.NotificationSuccessColor,
    val lazyColumnState: LazyListState = LazyListState(),
    var lazyColumnPosition:Int = 0,
    var test:Int = 0,
    val currentReport:Report = Report(),
    var isNavigationDrawerOpen:Boolean = false,
    var recomposeTrigger:Int = 0,
    var bottomSheetType:BottomSheetBodyType = BottomSheetBodyType.ALL_OPTIONS,
    var appLanguage: String = "",
    var isAppLanguageLoading:Boolean = true,
    var isNotificationMessageShown:Boolean = false,
    var isUserSignedIn:Boolean = AppData.loggedInUser != null,

    )
package com.anas.weeklyreport.data

import android.net.Uri
import androidx.compose.foundation.lazy.LazyListState
import com.anas.weeklyreport.model.Report
import com.anas.weeklyreport.shared.BottomSheetBodyType

data class HomeScreenState (
    val reports:MutableList<Report> = mutableListOf(),
    var documentUri: Uri? = null,
    var isBottomSheetShown:Boolean = false,
    var screen:String = "",
    var screenLoading:Boolean = false,
    var itemsLoading:Boolean = false,
    var toastMessage:String = "",
    val lazyColumnState: LazyListState = LazyListState(),
    var lazyColumnPosition:Int = 0,
    var test:Int = 0,
    val currentReport:Report = Report(),
    var isNavigationDrawerOpen:Boolean = false,
    var recomposeTrigger:Int = 0,
    var bottomSheetType:BottomSheetBodyType = BottomSheetBodyType.ALL_OPTIONS,
    var appLanguage: String = "",
    var isAppLanguageLoading:Boolean = true,
    var isNotificationMessageShown:Boolean = false

)
package com.anas.weeklyreport.screen_actions

import com.anas.weeklyreport.model.Report
import com.anas.weeklyreport.shared.BottomSheetBodyType

sealed interface HomeScreenEvent{
    object OnCreateNewDocumentClick:HomeScreenEvent
    data class OnDocumentItemClick(val id: String): HomeScreenEvent
    object OnDismissBottomSheet:HomeScreenEvent
    data class OnOpenBottomSheet(val report: Report):HomeScreenEvent
    object OnMoveToTrashClick:HomeScreenEvent
    object OnBookmarkReport:HomeScreenEvent
    object OnDownloadReport:HomeScreenEvent
    data class RequestNavigationDrawer(val isOpen:Boolean):HomeScreenEvent
    object OnBookmarkDrawerClick:HomeScreenEvent
    object OnTrashDrawerClick:HomeScreenEvent
    object OnRestoreReportClick:HomeScreenEvent
    object OnDeleteReportClick:HomeScreenEvent
    data class ChangeBottomSheetType(val type:BottomSheetBodyType):HomeScreenEvent
    data class OnLanguageSelection(val language:String):HomeScreenEvent
    object OnLanguageDrawerClick:HomeScreenEvent
}
package com.anas.weeklyreport.viewmodels

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anas.weeklyreport.AppData
import com.anas.weeklyreport.shared.AppScreen
import com.anas.weeklyreport.AppData.allReports
import com.anas.weeklyreport.AppData.appLanguage
import com.anas.weeklyreport.data.DataState
import com.anas.weeklyreport.data.HomeScreenState
import com.anas.weeklyreport.data.repository.MyServerRepo
import com.anas.weeklyreport.screen_actions.HomeScreenEvent
import com.anas.weeklyreport.shared.BottomSheetBodyType
import com.anas.weeklyreport.shared.ReportActionType.BOOKMARK
import com.anas.weeklyreport.shared.ReportActionType.RESTORE
import com.anas.weeklyreport.shared.ReportActionType.TRASH
import com.anas.weeklyreport.shared.ReportListType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@SuppressLint("MutableCollectionMutableState")
@HiltViewModel
class HomeViewmodel @Inject constructor(
    private val myServer: MyServerRepo
):ViewModel() {

    var reports by allReports
    private var isPageInitialized = false
    val state = MutableStateFlow(HomeScreenState())

    fun init(deviceLanguage: String) {
        if (!isPageInitialized){
            getAppLanguage(deviceLanguage)
            getAllReports()
            isPageInitialized = true
        }
    }

    fun onEvent(event: HomeScreenEvent){
        when(event){
            HomeScreenEvent.OnCreateNewDocumentClick -> {
                state.update { body ->
                    body.copy(
                        screen = AppScreen.CREATE_DOCUMENT_SCREEN.toString() + "/empty"
                    )
                }
            }
            is HomeScreenEvent.OnDocumentItemClick -> {
                state.update { body ->
                    body.copy(
                        screen = AppScreen.CREATE_DOCUMENT_SCREEN.toString() + "/${event.id}",
                        lazyColumnPosition = reports.indexOf(reports.find { it.id == event.id })
                    )
                }
            }
            HomeScreenEvent.OnDismissBottomSheet -> {
                state.update { body ->
                    body.copy(
                        bottomSheetType = BottomSheetBodyType.ALL_OPTIONS,
                        isBottomSheetShown = false
                    )
                }

            }
            is HomeScreenEvent.OnOpenBottomSheet ->  {
                state.update { body ->
                    body.copy(
                        currentReport = event.report,
                        isBottomSheetShown = true
                    )
                }
            }
            HomeScreenEvent.OnBookmarkReport -> addReportToBookmark()
            HomeScreenEvent.OnMoveToTrashClick -> moveReportToTrash()
            HomeScreenEvent.OnDownloadReport -> getDocument(state.value.currentReport.id)
            is HomeScreenEvent.RequestNavigationDrawer -> {
                state.update { body ->
                    body.copy(
                        isNavigationDrawerOpen = event.isOpen,
                    )
                }
            }
            HomeScreenEvent.OnBookmarkDrawerClick -> {
                state.update { body ->
                    body.copy(
                        isNavigationDrawerOpen = false,
                        screen = AppScreen.REPORT_LIST_SCREEN.toString() + "/${ReportListType.BOOKMARKS}",
                    )
                }
            }
            HomeScreenEvent.OnTrashDrawerClick -> {
                state.update { body ->
                    body.copy(
                        isNavigationDrawerOpen = false,
                        screen = AppScreen.REPORT_LIST_SCREEN.toString() + "/${ReportListType.TRASH}",
                    )
                }
            }
            HomeScreenEvent.OnDeleteReportClick -> deleteItem()
            HomeScreenEvent.OnRestoreReportClick -> {restoreReport()}
            is HomeScreenEvent.ChangeBottomSheetType -> {
                state.update { body ->
                    body.copy(
                        bottomSheetType = event.type
                    )
                }
            }

            HomeScreenEvent.OnLanguageDrawerClick -> {
                state.update { body ->
                    body.copy(
                        bottomSheetType = BottomSheetBodyType.CHANGE_LANGUAGE,
                        isNavigationDrawerOpen = false,
                        isBottomSheetShown = true
                    )
                }
            }

            is HomeScreenEvent.OnLanguageSelection -> {
                state.update { body ->
                    body.copy(
                        bottomSheetType = BottomSheetBodyType.ALL_OPTIONS,
                        isBottomSheetShown = false
                    )
                }
                saveAppLanguageToCache(event.language)

            }

            is HomeScreenEvent.RequestNotificationMessage -> state.update { body -> body.copy(isNotificationMessageShown = event.isShown) }
        }
    }

    private fun saveAppLanguageToCache(languageCode:String) {
        myServer.saveDataToLocalCache(languageCode, "app_language").onEach { stateFlow ->
            when(stateFlow){
                is DataState.Success -> {
                    appLanguage = languageCode

                    state.update { body ->
                        body.copy(
                            appLanguage = languageCode,
                            toastMessage = "",
                            screenLoading = false
                        )
                    }
                }
                is DataState.Error -> {
                    state.update { body ->
                        body.copy(
                            toastMessage = "Failed to change language ",
                            screenLoading = false
                        )
                    }
                }
                DataState.Loading ->{
                    state.update { body ->
                        body.copy(
                            screenLoading = true
                        )
                    }
                }
            }

        }.launchIn(viewModelScope)
    }

    private fun restoreReport() {
        state.value.currentReport.isInTrash = false
        saveReport(RESTORE)
    }

    private fun saveReport(action:String){
        var toastMessage = ""
        viewModelScope.launch {
            myServer.saveReport(state.value.currentReport).onEach { stateFlow ->
                when(stateFlow){
                    is DataState.Success -> {
                        when(action){
                            TRASH ->{
                                toastMessage = "Report moved to trash"
                            }
                            BOOKMARK -> {
                                toastMessage = if (stateFlow.data.isBookmarked)
                                    "Report added to bookmarks"
                                else
                                    "Report removed from bookmarks"
                            }
                            RESTORE -> {
                                toastMessage = "Report restored"
                            }

                        }
                        state.update { body ->
                            body.copy(
                                isNotificationMessageShown = true,
                                toastMessage = toastMessage,
                                screenLoading = false
                            )
                        }
                    }
                    is DataState.Error -> {
                        when(action){ // roll back any changes
                            TRASH ->{
                                state.value.currentReport.isInTrash = false
                            }
                            BOOKMARK -> {
                                state.value.currentReport.isBookmarked = !state.value.currentReport.isBookmarked
                            }
                            RESTORE -> {
                                state.value.currentReport.isInTrash = true
                            }

                        }
                        state.update { body ->
                            body.copy(
                                toastMessage = "Failed to update item",
                                screenLoading = false
                            )
                        }
                    }
                    DataState.Loading -> {
                        state.update { body ->
                            body.copy(
                                screenLoading = true
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun getAppLanguage(deviceLanguage: String) {
        appLanguage = myServer.getDataFromLocalCache(AppData.sharedPreferencesLanguageKey) ?: deviceLanguage
        state.update { body ->
            body.copy(
                isAppLanguageLoading = false,
                appLanguage = appLanguage
            )
        }
    }

    private fun addReportToBookmark() {
        state.value.currentReport.isBookmarked = !state.value.currentReport.isBookmarked
        saveReport(BOOKMARK)
    }


    private fun moveReportToTrash(){
        state.value.currentReport.isInTrash = true
        saveReport(TRASH)
    }
    private fun deleteItem() {
        viewModelScope.launch {
            myServer.deleteReport(state.value.currentReport.id).onEach {stateFlow->
                when(stateFlow){
                    is DataState.Success -> {
                        reports.removeIf { it.id == state.value.currentReport.id }
                        state.update { body ->
                            body.copy(
                                toastMessage = "Report deleted",
                                screenLoading = false
                            )
                        }
                    }
                    is DataState.Error -> {
                        state.update { body ->
                            body.copy(
                                toastMessage = "Failed to delete report",
                                screenLoading = false
                            )
                        }
                    }
                    DataState.Loading -> {
                        state.update { body ->
                            body.copy(
                                screenLoading = true
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun getDocument(reportId:String){
        viewModelScope.launch {
            myServer.getDocument(reportId, state.value.currentReport.name).onEach { stateFlow ->
                when(stateFlow){
                    is DataState.Success -> {
                        state.update { body ->
                            body.copy(
                                documentUri = stateFlow.data,
                                screenLoading = false
                            )
                        }
                    }
                    is DataState.Error -> {
                        state.update { body ->
                            body.copy(
                                toastMessage = stateFlow.exception.message!!,
                                screenLoading = false
                            )
                        }
                    }
                    DataState.Loading -> {
                        state.update { body ->
                            body.copy(
                                screenLoading = true
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
    private fun getAllReports(){
        viewModelScope.launch {
            myServer.getAllReports().onEach { stateFlow ->
                when(stateFlow){
                    is DataState.Success -> {
                        reports.addAll(stateFlow.data.sortedByDescending { it.createdAt })
                        state.update { body ->
                            body.copy(
                                itemsLoading = false
                            )
                        }
                    }
                    is DataState.Error -> {
                        state.update { body ->
                            body.copy(
                                toastMessage = stateFlow.exception.message!!,
                                itemsLoading = false
                            )
                        }
                    }
                    DataState.Loading -> {
                        state.update { body ->
                            body.copy(
                                itemsLoading = true
                            )
                        }
                    }

                }
            }.launchIn(viewModelScope)
        }
    }

    fun forceRecomposition() {
        state.update { body ->
            body.copy(
                recomposeTrigger = body.recomposeTrigger +1
            )
        }
    }

}




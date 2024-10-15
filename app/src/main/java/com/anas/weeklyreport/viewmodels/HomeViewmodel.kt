package com.anas.weeklyreport.viewmodels

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anas.weeklyreport.AppData
import com.anas.weeklyreport.shared.AppScreen
import com.anas.weeklyreport.AppData.allReports
import com.anas.weeklyreport.AppData.appLanguage
import com.anas.weeklyreport.AppData.loggedInUser
import com.anas.weeklyreport.R
import com.anas.weeklyreport.data.Result
import com.anas.weeklyreport.data.HomeScreenState
import com.anas.weeklyreport.data.repository.ReportRepository
import com.anas.weeklyreport.data.repository.UserRepository
import com.anas.weeklyreport.extension_methods.getYear
import com.anas.weeklyreport.model.User
import com.anas.weeklyreport.screen_actions.HomeScreenEvent
import com.anas.weeklyreport.shared.AppColors
import com.anas.weeklyreport.shared.BottomSheetBodyType
import com.anas.weeklyreport.shared.NetworkException
import com.anas.weeklyreport.shared.ReportActionType.BOOKMARK
import com.anas.weeklyreport.shared.ReportActionType.RESTORE
import com.anas.weeklyreport.shared.ReportActionType.TRASH
import com.anas.weeklyreport.shared.ReportListType
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@SuppressLint("MutableCollectionMutableState")
@HiltViewModel
class HomeViewmodel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val userRepository: UserRepository
):ViewModel() {

    var reports = allReports
    private var isPageInitialized = false
    val state = MutableStateFlow(HomeScreenState())
    var screen by mutableStateOf("")
    var filterCategories = arrayListOf(R.string.all)

    fun init(deviceLanguage: String) {
        initFilterCategories()
        if (loggedInUser == null){
            state.update { body -> body.copy(
                screen = AppScreen.USER_DETAILS.toString()
            ) }
           return
        }
        if (!isPageInitialized){
            getAllReports()
            state.update { body -> body.copy(
                loggedInUser = loggedInUser!!,
                isUserSignedIn = loggedInUser!!.id.isNotBlank()
            ) }
            isPageInitialized = true

        }

        getAppLanguage(deviceLanguage)
    }

    private fun initFilterCategories() {
//         reports.value.sortedByDescending { it.fromDate.convertStringToDate("dd/MM/yyyy") }.forEach {
//             if (it.fromDate.isNotBlank() && !it.isInTrash && !filterCategories.contains(it.fromDate.getYear().toInt())){
//                 filterCategories.add(it.fromDate.getYear().toInt())
//             }
//         }
        reports.value.forEach {
             if (it.fromDate.isNotBlank() && !it.isInTrash && !filterCategories.contains(it.fromDate.getYear().toInt())){
                 filterCategories.add(it.fromDate.getYear().toInt())
             }
         }
        // sort the list and ignore the first item
        filterCategories = ArrayList((arrayListOf(filterCategories[0]) + filterCategories.drop(1).sortedByDescending { it }))
    }

    fun onEvent(event: HomeScreenEvent){
        when(event){
            HomeScreenEvent.OnCreateNewDocumentClick -> {
                state.update { body ->
                    body.copy(
                        screen = AppScreen.CREATE_REPORT_SCREEN.toString() + "/empty"
                    )
                }
            }
            is HomeScreenEvent.OnDocumentItemClick -> {
                state.update { body ->
                    body.copy(
                        screen = AppScreen.CREATE_REPORT_SCREEN.toString() + "/${event.id}",
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
            is HomeScreenEvent.OnReportItemMoreActionClick ->  {
                state.update { body ->
                    body.copy(
                        currentReport = event.report,
                        bottomSheetType = if (event.report.isInTrash) BottomSheetBodyType.TRASH_OPTIONS else BottomSheetBodyType.ALL_OPTIONS,
                        isBottomSheetShown = true
                    )
                }
            }
            HomeScreenEvent.OnBookmarkReport -> addReportToBookmark()
            HomeScreenEvent.OnMoveToTrashClick -> moveReportToTrash()
            HomeScreenEvent.OnDownloadReport -> getDocument()
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
//                        isNavigationDrawerOpen = false,
                        screen = AppScreen.REPORT_LIST_SCREEN.toString() + "/${ReportListType.BOOKMARKS}",
                    )
                }
            }
            HomeScreenEvent.OnTrashDrawerClick -> {
                state.update { body ->
                    body.copy(
//                        isNavigationDrawerOpen = false,
                        screen = AppScreen.REPORT_LIST_SCREEN.toString() + "/${ReportListType.TRASH}",
                    )
                }
            }
            HomeScreenEvent.OnDeleteReportClick -> deleteItem()
            HomeScreenEvent.OnRestoreReportClick -> restoreReport()
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
                        isBottomSheetShown = false
                    )
                }
                saveAppLanguageToCache(event.language)
            }

            is HomeScreenEvent.RequestNotificationMessage -> state.update { body -> body.copy(isNotificationMessageShown = event.isShown) }
            is HomeScreenEvent.OnGoogleSignInResult -> onGoogleSignInResult(event.result)
            is HomeScreenEvent.OnSignOutClick -> signOutUser()
            HomeScreenEvent.OnEditProfileClick -> state.update { body ->
                body.copy(
                    screen = AppScreen.USER_DETAILS.toString(),
                )
            }

            HomeScreenEvent.OnSettingsClick -> {
                state.update { body ->
                    body.copy(
                        isNavigationDrawerOpen = false,
                        screen = AppScreen.SETTINGS.toString()
                    )
                }
            }

            is HomeScreenEvent.FilterReports -> {
                state.update { body ->
                    body.copy(
                        filterReportsBy = event.label
                    )
                }

                when(state.value.filterReportsBy){
                     R.string.all -> {}
                }
            }
        }
    }
    private fun signInUser(user: User){
        viewModelScope.launch {
            userRepository.signUp(user).onEach { stateFlow ->
                when(stateFlow){
                    is Result.Success -> {

                        state.update { body -> body.copy(
                            screenLoading = false,
                            loggedInUser = stateFlow.data,
                            notificationMessage = R.string.signed_in_successfully,
                            notificationColor = AppColors.NotificationSuccessColor,
                            isUserSignedIn = true,
                            isNotificationMessageShown = true
                        )}
                        getAllReports()
                    }
                    is Result.Error -> {
                        state.update { body -> body.copy(
                            notificationMessage = R.string.unknownErrorMessage,
                            notificationColor = AppColors.NotificationErrorColor,
                            isNotificationMessageShown = true,
                            screenLoading = false
                        ) }
                    }
                    Result.Loading -> {
                        state.update { body -> body.copy(
                            isNavigationDrawerOpen = false,
                            screenLoading = true
                        ) }
                    }
                }
            }.launchIn(viewModelScope)

        }
    }
    private fun signOutUser(){
        state.update { body -> body.copy(
            isNavigationDrawerOpen = false,
            screenLoading = true,
            isUserSignedIn = false,
            loggedInUser = User()
        ) }
        viewModelScope.launch {
            delay(600)
            reportRepository.deleteAllLocalReports()
            userRepository.signOut()
            state.update { body -> body.copy(
                screenLoading = false,
                screen = AppScreen.USER_DETAILS.toString()
            ) }
        }
    }


    private fun saveAppLanguageToCache(languageCode:String) {
        when(reportRepository.saveDataToLocalCache(languageCode, "app_language")){
            true -> {
                appLanguage = languageCode

                state.update { body ->
                    body.copy(
                        appLanguage = languageCode,
                        screenLoading = false
                    )
                }
            }
            false -> {
                state.update { body ->
                    body.copy(
                        notificationMessage = R.string.failedToChangeLanuag,
                        isNotificationMessageShown = true,
                        screenLoading = false
                    )
                }
            }

        }
    }
    private fun restoreReport() {
        state.value.currentReport.isInTrash = false
        saveReport(RESTORE)
    }

    private fun onGoogleSignInResult(result: ActivityResult){

        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val googleUser = loggedInUser?.copy(
                    id = account.id!!,
                    fullName = account.displayName!!,
                    email = account.email!!
                )
                signInUser(googleUser!!) // remote sign in
            } catch (e: ApiException) {
                Log.e("Google SignIn Result", e.message, e)
                state.update { body -> body.copy(
                    notificationMessage = R.string.unknownErrorMessage,
                    notificationColor = AppColors.NotificationErrorColor,
                    isNotificationMessageShown = true
                )}
            }
        }else{
            println(result.resultCode)
            state.update { body -> body.copy(
                notificationMessage = R.string.google_sign_in_failed,
                notificationColor = AppColors.NotificationErrorColor,
                isNotificationMessageShown = true
            )}
        }
    }

    private fun saveReport(action:String){
        var toastMessage = 0
        viewModelScope.launch {
            reportRepository.updateReport(state.value.currentReport).onEach { stateFlow ->
                when(stateFlow){
                    is Result.Success -> {
                        when(action){
                            TRASH ->{
                                toastMessage = R.string.report_moved_to_trash
                            }
                            BOOKMARK -> {
                                toastMessage = if (state.value.currentReport.isBookmarked)
                                    R.string.report_moved_to_bookmarks
                                else
                                    R.string.report_removed_to_bookmarks
                            }
                            RESTORE -> {
                                toastMessage = R.string.report_restored
                            }

                        }
                        state.update { body ->
                            body.copy(
                                notificationColor = AppColors.NotificationSuccessColor,
                                notificationMessage = toastMessage,
                                isNotificationMessageShown = true,
                                screenLoading = false
                            )
                        }
                    }
                    is Result.Error -> {
                        if (stateFlow.exception is NetworkException){
                            toastMessage = R.string.having_trouble_connecting_error_message
                        }else{
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
                            toastMessage = R.string.failed_to_update_report
                        }

                        state.update { body ->
                            body.copy(
                                notificationMessage = toastMessage,
                                notificationColor = AppColors.NotificationErrorColor,
                                isNotificationMessageShown = true,
                                screenLoading = false
                            )
                        }
                    }
                    Result.Loading -> {
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

        appLanguage = reportRepository.getDataFromLocalCache(AppData.sharedPreferencesLanguageKey) ?: deviceLanguage
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
        state.value.currentReport.isDeleted = true
        viewModelScope.launch {
            reportRepository.deleteReport(state.value.currentReport.id).onEach { stateFlow->
                when(stateFlow){
                    is Result.Success -> {
                        state.update { body ->
                            body.copy(
                                notificationMessage = R.string.reportDeletedMessage,
                                notificationColor = AppColors.NotificationSuccessColor,
                                isNotificationMessageShown = true,
                                screenLoading = false
                            )
                        }
                    }
                    is Result.Error -> {
                        val message  = when(stateFlow.exception){
                            is NetworkException -> R.string.network_error_message
                            else -> {
                                state.value.currentReport.isDeleted = false
                                R.string.unknownErrorMessage
                            }
                        }

                        state.update { body ->
                            body.copy(
                                notificationMessage = message,
                                notificationColor = AppColors.NotificationErrorColor,
                                isNotificationMessageShown = true,
                                screenLoading = false
                            )
                        }
                    }
                    Result.Loading -> {
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

    private fun getDocument(){
        viewModelScope.launch {
            reportRepository.getDocument(state.value.currentReport).onEach { stateFlow ->
                when(stateFlow){
                    is Result.Success -> {
                        state.update { body ->
                            body.copy(
                                documentUri = stateFlow.data,
                                screenLoading = false
                            )
                        }
                    }
                    is Result.Error -> {
                        state.update { body ->
                            body.copy(
                                notificationMessage = R.string.documnetDownloadFailMessage ,
                                notificationColor = AppColors.NotificationErrorColor,
                                isNotificationMessageShown = true,
                                screenLoading = false
                            )
                        }
                    }
                    Result.Loading -> {
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
            reportRepository.getAllReports().onEach { stateFlow ->
                when(stateFlow){
                    is Result.Success -> {
                        initFilterCategories()
                        state.update { body ->
                            body.copy(
                                itemsLoading = false
                            )
                        }
                    }
                    is Result.Error -> {
                       val message = when(stateFlow.exception){
                           is NetworkException -> {
                               R.string.reports_network_error_message
                           }
                            else -> {
                                R.string.unknownErrorMessage
                            }
                        }
                        state.update { body ->
                            body.copy(
                                notificationMessage = message,
                                notificationColor = AppColors.NotificationErrorColor,
                                isNotificationMessageShown = true,
                                itemsLoading = false
                            )
                        }
                    }
                    Result.Loading -> {
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




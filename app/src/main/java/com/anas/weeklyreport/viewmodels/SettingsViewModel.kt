package com.anas.weeklyreport.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anas.weeklyreport.AppData
import com.anas.weeklyreport.R
import com.anas.weeklyreport.data.Result
import com.anas.weeklyreport.data.SettingsScreenState
import com.anas.weeklyreport.data.repository.ReportRepository
import com.anas.weeklyreport.data.repository.UserRepository
import com.anas.weeklyreport.screen_actions.SettingsScreenEvent
import com.anas.weeklyreport.shared.AppColors
import com.anas.weeklyreport.shared.AppScreen
import com.anas.weeklyreport.shared.BottomSheetBodyType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel  @Inject constructor(
    private val reportRepository: ReportRepository,
    private val userRepository: UserRepository
):ViewModel() {
    val state = MutableStateFlow(SettingsScreenState())

    fun onEvent(event:SettingsScreenEvent){
        when(event){
            SettingsScreenEvent.OnLanguageClick -> {
                state.update { body ->
                    body.copy(
                        bottomSheetType = BottomSheetBodyType.CHANGE_LANGUAGE,
                        isBottomSheetShown = true
                    )
                }
            }
            is SettingsScreenEvent.RequestBottomSheet -> {state.update { body ->
                body.copy(
                    isBottomSheetShown = event.isShown
                )
            }}
            is SettingsScreenEvent.OnLanguageSelection -> {
                state.update { body ->
                    body.copy(
                        isBottomSheetShown = false
                    )
                }
                saveAppLanguageToCache(event.language)
            }

            SettingsScreenEvent.OnProfileClick -> {
                state.update { body ->
                    body.copy(
                        screen = AppScreen.USER_DETAILS.toString(),
                    )
                }
            }

            SettingsScreenEvent.OnConfirmAccountDelete -> deleteUser()
            SettingsScreenEvent.OnDeleteClick -> {
                state.update { body ->
                    body.copy(
                        isDeleteConfirmationDialogShown = true,
                    )
                }
            }

            is SettingsScreenEvent.RequestDeleteDialog ->  {
                state.update { body ->
                    body.copy(
                        isDeleteConfirmationDialogShown = event.isShown,
                    )
                }
            }

            is SettingsScreenEvent.RequestNotificationMessage -> {
                state.update { body ->
                    body.copy(
                        isNotificationMessageShown = event.isShown,
                    )
                }
            }

            SettingsScreenEvent.OnPrivacyPolicyClick -> {

            }

            SettingsScreenEvent.OnFeedbackClick -> state.update { body ->
                body.copy(
                   screen = AppScreen.FEEDBACK.toString()
                )
            }
        }
    }

    private fun deleteUser() {
        viewModelScope.launch {
            userRepository.deleteUserById(AppData.loggedInUser?.id!!).onEach { stateFlow ->
                when(stateFlow){
                    is Result.Success -> {
                        reportRepository.deleteAllLocalReports()
                        state.update { body -> body.copy(
                            screenLoading = false,
                            notificationMessageColor = Color.Transparent,
                            screen = AppScreen.HOME_SCREEN.toString()
                        ) }
                    }
                    is Result.Error -> {
                        state.update { body -> body.copy(
                            notificationMessage = R.string.account_delete_error_message,
                            notificationMessageColor = AppColors.NotificationErrorColor,
                            isNotificationMessageShown = true,
                            screenLoading = false
                        ) }
                    }
                    Result.Loading -> {
                        state.update { body -> body.copy(
                            screenLoading =true
                        ) }
                    }
                }
            }.launchIn(viewModelScope)
        }

    }

    private fun saveAppLanguageToCache(languageCode: String) {
        when(reportRepository.saveDataToLocalCache(languageCode, "app_language")){
            true -> {
                AppData.appLanguage = languageCode

                state.update { body ->
                    body.copy(
                        appLanguage = languageCode,
                        recompositionTrigger = body.recompositionTrigger +1
                    )
                }
            }
            false -> {
            }

        }
    }
    fun forceRecomposition() {
        state.update { body ->
            body.copy(
                recompositionTrigger = body.recompositionTrigger +1
            )
        }
    }
}
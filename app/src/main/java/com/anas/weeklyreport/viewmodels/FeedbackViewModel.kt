package com.anas.weeklyreport.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anas.weeklyreport.R
import com.anas.weeklyreport.data.FeedbackScreenState
import com.anas.weeklyreport.data.Result
import com.anas.weeklyreport.data.repository.EmailRepository
import com.anas.weeklyreport.model.Email
import com.anas.weeklyreport.screen_actions.FeedbackScreenEvent
import com.anas.weeklyreport.shared.AppColors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val emailRepository: EmailRepository
) : ViewModel() {

    val state = MutableStateFlow(FeedbackScreenState())

   fun onEvent(event:FeedbackScreenEvent){
       when(event){
           is FeedbackScreenEvent.OnEmailValueChange -> {
               state.update { body ->
                  body.copy(
                         email = event.value
                   )
               }
           }
           is FeedbackScreenEvent.OnMessageValueChange -> state.update { body ->
               body.copy(
                   message = event.value
               )
           }
           is FeedbackScreenEvent.RequestNotificationMessage -> {
               state.update { body ->
                   body.copy(
                       isNotificationMessageShown = event.isShown
                   )
               }
           }

           FeedbackScreenEvent.OnSaveClick -> sendEmail()
           is FeedbackScreenEvent.SelectEmoji -> {
               state.update { body ->
                   body.copy(
                       selectedEmoji = if (event.emoji == body.selectedEmoji) "" else event.emoji
                   )
               }
           }
       }
   }

    private fun sendEmail() {
        val message = "A feedback was sent from user: ${state.value.email.ifBlank { "unknown" }}" +
                "\n\nUser reaction is: ${state.value.selectedEmoji}" +
                "\n\n${state.value.message}"

        viewModelScope.launch {
            val email = Email(
                toEmail = "weekly.report.app0@gmail.com",
                message = message,
                subject = "User Feedback"
            )
            emailRepository.sendEmail(email).onEach { stateFlow ->
                when(stateFlow){
                    is Result.Success -> {
                        state.update { body ->
                            body.copy(
                                screenLoading =false,
                                notificationMessageColor = AppColors.NotificationSuccessColor,
                                notificationMessage = R.string.email_sent_successfully,
                                isNotificationMessageShown = true
                            )
                        }
                        delay(1500)
                        state.update { body ->
                            body.copy(
                                goBack = true
                            )
                        }
                    }
                    is Result.Error -> {
                        state.update { body ->
                            body.copy(
                                screenLoading =false,
                                notificationMessageColor = AppColors.NotificationErrorColor,
                                notificationMessage = R.string.unknownErrorMessage,
                                isNotificationMessageShown = true
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


}
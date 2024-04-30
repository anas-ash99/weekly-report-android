package com.anas.weeklyreport.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anas.weeklyreport.AppData.loggedInUser

import com.anas.weeklyreport.R
import com.anas.weeklyreport.data.Result
import com.anas.weeklyreport.data.UserDetailsStates
import com.anas.weeklyreport.data.repository.UserRepository
import com.anas.weeklyreport.model.User
import com.anas.weeklyreport.screen_actions.UserDetailEvent
import com.anas.weeklyreport.shared.AppColors
import com.anas.weeklyreport.shared.AppScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UserDetailsViewModel @Inject constructor(
    private val userRepository:UserRepository
):ViewModel() {

    val state =  MutableStateFlow(UserDetailsStates())
    val isNewUser = loggedInUser == null

    init {
        if (loggedInUser != null){
            state.update { state -> state.copy(
                nameTextField = loggedInUser?.fullName!!,
                companyField = loggedInUser?.company!!,
                departmentField = loggedInUser?.ausbildungDepartment!!,
                emailTextField = loggedInUser?.email!!
            ) }
        }
    }

       fun onEvent(event:UserDetailEvent){
           when(event){
               is UserDetailEvent.OnTextFieldValueChange -> handleValueChange(event.value, event.label)
               UserDetailEvent.OnSaveClick -> updateUser()
               is UserDetailEvent.RequestNotificationMessage -> {
                   state.update { body -> body.copy(isNotificationMessageShown = event.isShown) }
               }
           }
       }

    private fun updateUser() {
        val user = if (loggedInUser != null){
            loggedInUser?.copy(
                fullName = state.value.nameTextField,
                company = state.value.companyField,
                email = state.value.emailTextField,
                ausbildungDepartment = state.value.departmentField,
            )
        }else{
            User(
                fullName = state.value.nameTextField,
                company = state.value.companyField,
                email = state.value.emailTextField,
                ausbildungDepartment = state.value.departmentField,
            )
        }
        viewModelScope.launch {
            userRepository.updateUser(user!!).onEach { stateFlow ->
                when(stateFlow){
                    is Result.Error -> {
                        state.update { body -> body.copy(
                            screenLoading =false,
                            notificationMessageColor = AppColors.NotificationErrorColor,
                            notificationMessage = R.string.unknownErrorMessage,
                            isNotificationMessageShown = true
                        ) }
                    }
                    Result.Loading -> { state.update { body -> body.copy(screenLoading = true) }}
                    is Result.Success -> {
                        println("success .. ")
                        state.update { body -> body.copy(
                            screenLoading =false,
                            screen = AppScreen.HOME_SCREEN.toString()
                        ) }
                    }
                }
            }.launchIn(viewModelScope)

        }
    }

    private fun handleValueChange(value:String, label:Int){
        when(label){
            R.string.full_name_label -> state.update {
                it.copy(
                    nameTextField = value
                )
            }
            R.string.company_label -> state.update {
                it.copy(
                    companyField = value
                )
            }
            R.string.department_label -> state.update {
                it.copy(
                    departmentField = value
                )
            }
            R.string.email_label -> state.update {
                it.copy(
                    emailTextField = value
                )
            }

        }
    }
}
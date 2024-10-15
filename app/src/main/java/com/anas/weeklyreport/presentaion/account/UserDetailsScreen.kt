package com.anas.weeklyreport.presentaion.account

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.anas.weeklyreport.AppData.loggedInUser
import com.anas.weeklyreport.R
import com.anas.weeklyreport.presentaion.LoadingSpinner
import com.anas.weeklyreport.presentaion.MyNotificationMessage
import com.anas.weeklyreport.presentaion.home.MyProfilePic
import com.anas.weeklyreport.screen_actions.UserDetailEvent
import com.anas.weeklyreport.shared.AppColors
import com.anas.weeklyreport.shared.AppScreen
import com.anas.weeklyreport.viewmodels.UserDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailsScreen(navController: NavController?) {

    val viewModel = hiltViewModel<UserDetailsViewModel>()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.screen){
        if (state.screen.isBlank()){
            return@LaunchedEffect
        }
        if (state.screen == AppScreen.HOME_SCREEN.toString() && !viewModel.isNewUser ){
            navController?.popBackStack()
        }else{
            navController?.navigate(state.screen){
                //navController.graph.findStartDestination().id
                popUpTo(AppScreen.USER_DETAILS.toString()) { // remove the current screen from the back stack
                    inclusive = true
                }
            }
        }
        state.screen = ""
    }

    val textFields = listOf(
        UserDetailsTextField(
            value = state.nameTextField,
            label =R.string.full_name_label,
            icon = R.drawable.outline_person_outline_24

        ),
        UserDetailsTextField(
            value = state.companyField,
            label =R.string.company_label,
            icon = R.drawable.outline_work_outline_24

        ),
        UserDetailsTextField(
            value = state.departmentField,
            label =R.string.department_label,
            icon = R.drawable.workplace_home_company
        ),
        UserDetailsTextField(
            value = state.emailTextField,
            label =R.string.email_label,
            icon = R.drawable.outline_email_24
        ),

    )

    Surface {
        Scaffold(
            topBar = {
                Column {
                    if (!viewModel.isNewUser){
                        TopAppBar(
                            title = {
                                Text(text = stringResource(id = R.string.edited_profile_title)
                                )},
                            navigationIcon = {
                                IconButton(onClick = { navController?.popBackStack()}) {
                                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back" )
                                }}
                        )
                        Divider()
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = if (viewModel.isNewUser) Arrangement.Center else Arrangement.Top,
                horizontalAlignment = CenterHorizontally
            ) {

                if (viewModel.isNewUser){
                    Text(text = "Welcome",
                        fontSize = 50.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xB0000000)
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                    Text(text = "Help us customize your journey, share some info.",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }else{
                    Spacer(modifier = Modifier.height(20.dp))
//                    Image(
//                        modifier = Modifier
//                            .size(80.dp)
//                            .clip(CircleShape),
//                        painter = painterResource(id = R.drawable.user_icon),
//                        contentDescription = "App Icon"
//                    )
                    MyProfilePic( modifier = Modifier.size(90.dp), iconSize = 60.dp)
                    Spacer(modifier = Modifier.height(15.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))


                textFields.forEach { tf ->
                    if (tf.label != R.string.email_label || !(loggedInUser == null || loggedInUser!!.id.isBlank())) {
                        MyTextField(value = tf.value, label = tf.label , icon = tf.icon, onValueChange = {
                            viewModel.onEvent(UserDetailEvent.OnTextFieldValueChange(it, tf.label))
                        })
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(50.dp))
                Button(

                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .height(45.dp),
                    colors = ButtonDefaults.buttonColors(AppColors.APP_MAIN_COLOR),
                    onClick = { viewModel.onEvent(UserDetailEvent.OnSaveClick)},
                    enabled = (state.nameTextField.isNotBlank() && state.companyField.isNotBlank() && state.departmentField.isNotBlank())
                ) {
                    Text(text = "🖊️ Save" , fontSize = 18.sp)
                }

            }

        }
        MyNotificationMessage(
            message =  stringResource(id = state.notificationMessage  ),
            isVisible = state.isNotificationMessageShown,
            color = state.notificationMessageColor){
            viewModel.onEvent(UserDetailEvent.RequestNotificationMessage(false))
        }
        LoadingSpinner(isVisible = state.screenLoading)
    }
}



data class UserDetailsTextField(
    val value: String,
    val label:Int,
    val icon:Int,
)

@Composable
fun MyTextField(
    modifier: Modifier = Modifier,
    value: String,
    label:Int,
    icon:Int,
    onValueChange:(String) ->Unit
){
    TextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        shape = RoundedCornerShape(11.dp),
        readOnly = label == R.string.email_label ,
        enabled = label != R.string.email_label,
        textStyle = MaterialTheme.typography.titleMedium,
        placeholder = { Text(
            stringResource(id = label),
            color = Color(0xDF191919),
            ) },
        leadingIcon = {
            Icon(
                painterResource(id = icon) , contentDescription = "", Modifier.size(25.dp))
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0x2C5AA2D3),
            unfocusedContainerColor = Color(0x1D5AA2D3),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = AppColors.APP_MAIN_COLOR,
            focusedLabelColor = AppColors.APP_MAIN_COLOR,
            disabledContainerColor = Color(0x1D5AA2D3),
            disabledIndicatorColor = Color.Transparent,
            disabledTextColor = Color(0x92191919),
            disabledLeadingIconColor = Color(0x92191919)
        ),
        singleLine = true
    )
}
@Preview
@Composable
fun UserDetailsScreenPreview() {
    UserDetailsScreen(null)
}
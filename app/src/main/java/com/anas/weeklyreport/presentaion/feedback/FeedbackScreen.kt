package com.anas.weeklyreport.presentaion.feedback

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.anas.weeklyreport.R
import com.anas.weeklyreport.presentaion.LoadingSpinner
import com.anas.weeklyreport.presentaion.MyNotificationMessage
import com.anas.weeklyreport.screen_actions.FeedbackScreenEvent
import com.anas.weeklyreport.shared.AppColors
import com.anas.weeklyreport.viewmodels.FeedbackViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen (
    navController: NavController?
){
    val viewModel = hiltViewModel<FeedbackViewModel>()
    val state by viewModel.state.collectAsState()
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val density = LocalDensity.current
    val screenWidthPx = with(density) { screenWidthDp.toPx().roundToInt() }

    LaunchedEffect(state.goBack){
        if (state.goBack){
            navController?.popBackStack()
        }
    }
    val emojis = arrayListOf(
        Emoji(
            "😠",
            stringResource(id = R.string.very_bad)
        ),
        Emoji(
            "☹️",
            stringResource(id = R.string.bad)
        ),
        Emoji(
            "😐",
            stringResource(id = R.string.neutral)
        ),
        Emoji(
            "🙂",
            stringResource(id = R.string.good)
        ),
        Emoji(
            "😍",
            stringResource(id = R.string.very_good)
        ),
    )


   Surface {
       Scaffold (
           containerColor = Color.White,
           topBar = {
               TopAppBar(
                   navigationIcon = {
                        IconButton(onClick = { navController?.popBackStack()}) {
                          Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
                        }
                   },
                   title = {Text(text = stringResource(id = R.string.give_feedback) )},
                   colors = TopAppBarDefaults.topAppBarColors(
                       containerColor = Color.Transparent
                   )
               )
           }
       ){ innerPadding ->
            Column(
                Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 15.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = stringResource(id = R.string.email_label_optional), fontSize = 17.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.email,
                    textStyle = TextStyle(fontSize = 16.sp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    placeholder = { Text(text = stringResource(id = R.string.email_textfield_hint))},
                    onValueChange = {viewModel.onEvent(FeedbackScreenEvent.OnEmailValueChange(it.trim())) },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color(0x591A1A1A),
                        unfocusedPlaceholderColor = Color(0x751A1A1A),
                        focusedIndicatorColor = AppColors.APP_MAIN_COLOR,
                        focusedContainerColor = Color.Transparent,
                        cursorColor = AppColors.APP_MAIN_COLOR,
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))
                Text(text = stringResource(id = R.string.rate_your_experience), fontSize = 17.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(10.dp))

                Row (
                    Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment=  Alignment.CenterVertically,
                ){
                    emojis.forEach {
                        Surface(
                            modifier = Modifier
                                .size((screenWidthDp - 30.dp) / 5)
                                .clickable {
                                    viewModel.onEvent(FeedbackScreenEvent.SelectEmoji(it.text))
                                },
                            color = if (state.selectedEmoji == it.text ) Color.LightGray else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column (
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ){
                                Text(text = it.icon, fontSize = 25.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = it.text, fontSize = 12.sp)
                            }

                        }
                    }
                }
                Spacer(modifier = Modifier.height(70.dp))
                Text(text = stringResource(id = R.string.care_to_share_something), fontSize = 17.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth(),
                    value = state.message,
                    textStyle = TextStyle(fontSize = 16.sp),
                    placeholder = { Text(text = stringResource(id = R.string.feedback_textfild_hint))},
                    onValueChange = {viewModel.onEvent(FeedbackScreenEvent.OnMessageValueChange(it))},
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color(0x591A1A1A),
                        unfocusedPlaceholderColor = Color(0x751A1A1A),
                        focusedIndicatorColor = AppColors.APP_MAIN_COLOR,
                        focusedContainerColor = Color.Transparent,
                        cursorColor = AppColors.APP_MAIN_COLOR,
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp),
                        enabled = state.selectedEmoji.isNotBlank(),
                        onClick = { viewModel.onEvent(FeedbackScreenEvent.OnSaveClick) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.APP_MAIN_COLOR,
                            contentColor = Color.Unspecified
                        )

                    ) {
                        Text(text = stringResource(id = R.string.send_feedback), color = Color.White, fontSize = 16.sp)
                    }

                }
                Spacer(modifier = Modifier.height(20.dp))
            }
       }
       LoadingSpinner(isVisible = state.screenLoading)
       MyNotificationMessage(message = if (state.notificationMessage != 0) stringResource(id = state.notificationMessage) else "", isVisible = state.isNotificationMessageShown, color = state.notificationMessageColor){
           viewModel.onEvent(FeedbackScreenEvent.RequestNotificationMessage(false))
       }
   }


}
data class Emoji(
    val icon:String = "",
    val text:String = "",

)
@Preview
@Composable
fun FeedbackScreenPreview() {
    FeedbackScreen(navController = null)
}
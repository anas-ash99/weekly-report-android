package com.anas.weeklyreport.presentaion.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.anas.weeklyreport.AppData
import com.anas.weeklyreport.R
import com.anas.weeklyreport.extension_methods.setLocale
import com.anas.weeklyreport.presentaion.LoadingSpinner
import com.anas.weeklyreport.presentaion.MyNotificationMessage
import com.anas.weeklyreport.screen_actions.SettingsScreenEvent
import com.anas.weeklyreport.screen_actions.UserDetailEvent
import com.anas.weeklyreport.shared.AppScreen
import com.anas.weeklyreport.shared.BottomSheetBodyType
import com.anas.weeklyreport.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController?) {

    val viewModel = hiltViewModel<SettingsViewModel>()
    val state by viewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current
   // Create custom tabs intent fpr privacy policy
    val customTabsIntent = remember {
        CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()
    }


    // create intent to share app link for play store
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, AppData.privacyPolicyUrl)
        type = "text/plain"
    }

    LaunchedEffect(state.appLanguage){

        context.setLocale(state.appLanguage)
        viewModel.forceRecomposition()
    }
    LaunchedEffect(state.screen){
        if (state.screen == AppScreen.HOME_SCREEN.toString()){
            navController?.popBackStack()
            return@LaunchedEffect
        }
        if (state.screen.isNotBlank()){
            navController?.navigate(state.screen)
            state.screen = ""
        }
    }
    Surface {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFf6f7fb))
        ){
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFf6f7fb)
                ),
                title = { Text(text = stringResource(id = R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack()}) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color.White)

            ) {
                SettingsItem(stringResource(id = R.string.profile)){viewModel.onEvent(SettingsScreenEvent.OnProfileClick)}
                Divider(modifier = Modifier.padding(horizontal = 15.dp), color = Color(0xFFf6f6f6))
                SettingsItem(stringResource(id = R.string.language), subText = stringResource(id = languageCodes[state.appLanguage]!!)){viewModel.onEvent(SettingsScreenEvent.OnLanguageClick)}
            }


            Text(text = "${state.recompositionTrigger}", Modifier.size(0.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color.White)

            ) {
                SettingsItem(stringResource(id = R.string.rate_app)){
                    viewModel.onEvent(SettingsScreenEvent.OnFeedbackClick)
                }
                Divider(modifier = Modifier.padding(horizontal = 15.dp), color = Color(0xFFf6f6f6))
                SettingsItem(stringResource(id = R.string.share)){
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                }
                Divider(modifier = Modifier.padding(horizontal = 15.dp), color = Color(0xFFf6f6f6))
                SettingsItem(stringResource(id = R.string.privacy_policy)){
                    customTabsIntent.launchUrl(context, Uri.parse(AppData.privacyPolicyUrl))
                }
                AppData.loggedInUser?.let { user ->
                    if(user.id.isNotBlank()){
                        Divider(modifier = Modifier.padding(horizontal = 15.dp), color = Color(0xFFf6f6f6))
                        SettingsItem(stringResource(id = R.string.delete_account)){viewModel.onEvent(SettingsScreenEvent.OnDeleteClick)}
                    }
                }
            }

        }

        MyNotificationMessage(
            message = if (state.notificationMessage != 0) stringResource(id =state.notificationMessage) else "",
            color = state.notificationMessageColor,
            isVisible = state.isNotificationMessageShown,
            onTimeout = { viewModel.onEvent(SettingsScreenEvent.RequestNotificationMessage(false))})
        DeleteConfirmationDialog(
            openDialog = state.isDeleteConfirmationDialogShown,
            onConfirm = {
                viewModel.onEvent(SettingsScreenEvent.RequestDeleteDialog(false))
                viewModel.onEvent(SettingsScreenEvent.OnConfirmAccountDelete)
            },
            onDismissRequest = {viewModel.onEvent(SettingsScreenEvent.RequestDeleteDialog(false))}
        )
        LoadingSpinner(isVisible = state.screenLoading)
       SettingsBottomSheet(
           onDismissRequest = { viewModel.onEvent(SettingsScreenEvent.RequestBottomSheet(false))},
           isShown = state.isBottomSheetShown,
           sheetState = sheetState
       ) {
           when(state.bottomSheetType){
               BottomSheetBodyType.CHANGE_LANGUAGE -> {
                   ChangeLanguageSheetBody(onEvent = viewModel::onEvent )
               }
               else -> {}
           }
       }
    }

}


@Composable
fun DeleteConfirmationDialog(
    openDialog: Boolean,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit
) {
    if (openDialog) {
        AlertDialog(
            onDismissRequest =onDismissRequest,
            title = {
                Text(text = stringResource(id = R.string.delete_account))
            },
            text = {
                Text(stringResource(id = R.string.delete_account_diaolg_message))
            },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirm()
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismissRequest
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}


@Composable
fun SettingsItem(
    text:String,
    subText:String = "",
    onClick:() ->Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor= Color.Transparent,
            contentColor= Color.Black,
        ),
        contentPadding = PaddingValues(horizontal = 15.dp, vertical = 0.dp),
        shape = RoundedCornerShape(0.dp)
    ) {

        Row (
            modifier = Modifier.height(65.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            if (text == stringResource(id = R.string.language)){
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 9.dp),
                    verticalArrangement= Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.Start,
                ){
                    Text(
                        text = text,
                        fontSize= 18.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = if (text == stringResource(id = R.string.language)) Modifier.weight(1f) else Modifier
                    )
                    if (text == stringResource(id = R.string.language)){
                        Text(text = subText, color = Color(0x6A090909))
                    }
                }
            }else{
                Text(
                    text = text,
                    fontSize= 18.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.weight(1f)
                )
            }

            Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "arrow")
        }


    }
}
val languageCodes = hashMapOf(
    "en" to R.string.english,
    "de" to R.string.german
)

@Preview
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(null)
}
package com.anas.weeklyreport.presentaion.home.report_list


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.anas.weeklyreport.AppData.appLanguage
import com.anas.weeklyreport.extension_methods.capitalizeFirstLetter
import com.anas.weeklyreport.extension_methods.translateListType
import com.anas.weeklyreport.presentaion.LoadingSpinner
import com.anas.weeklyreport.presentaion.MyNotificationMessage
import com.anas.weeklyreport.presentaion.home.bottom_sheet.HomeBottomSheet
import com.anas.weeklyreport.screen_actions.HomeScreenEvent
import com.anas.weeklyreport.shared.AppColors
import com.anas.weeklyreport.shared.AppScreen
import com.anas.weeklyreport.shared.BottomSheetBodyType
import com.anas.weeklyreport.shared.ReportListType
import com.anas.weeklyreport.viewmodels.HomeViewmodel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportListScreen(
    navController: NavController?,
    type:String
) {
    val viewmodel: HomeViewmodel = hiltViewModel()
    val state by viewmodel.state.collectAsState()
    val reports by viewmodel.reports.collectAsState()
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()


    LaunchedEffect(state.screen){ // launchedEffect for handling navigation
        if (state.screen == AppScreen.HOME_SCREEN.toString()){
            navController?.popBackStack()
        }else if (state.screen.isNotBlank()){
            navController?.navigate(state.screen)
            state.screen = ""
        }
    }
    LaunchedEffect(state.documentUri){ // open the downloaded document
        state.documentUri?.let {
            openWordDocument(context, it)
            state.documentUri = null
        }
    }


    LaunchedEffect(Unit){
        state.itemsLoading = false
        if (type.lowercase() == "trash"){
            viewmodel.onEvent(HomeScreenEvent.ChangeBottomSheetType(BottomSheetBodyType.TRASH_OPTIONS))
        }else{
            viewmodel.onEvent(HomeScreenEvent.ChangeBottomSheetType(BottomSheetBodyType.ALL_OPTIONS))
        }
    }
    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val title = if (appLanguage == "de") type.translateListType() else type.lowercase().capitalizeFirstLetter()
            ReportListAppBar(title, onArrowBackClick = {navController?.popBackStack()})
            Divider(color = AppColors.DIVIDER, thickness = 1.dp, modifier = Modifier.padding(bottom = 15.dp))

            ReportList(
                state = state,
                onEvent = viewmodel::onEvent,
                reports = reports,
                type = type,
            )
        }
        MyNotificationMessage(
            message = if (state.notificationMessage != 0 ) stringResource(id = state.notificationMessage) else "",
            color = state.notificationColor,
            isVisible = state.isNotificationMessageShown){
            viewmodel.onEvent(HomeScreenEvent.RequestNotificationMessage(false))
        }
        LoadingSpinner(isVisible = state.screenLoading)
        HomeBottomSheet(state = state, sheetState = sheetState,  onEvent = viewmodel::onEvent)
    }

}
//val typeMap = mapOf("BOOKMARK" to "Bookmark", "TRASH" to "Trash", "")
@Preview
@Composable
fun ReportScreenListPreview(){
    ReportListScreen(null, ReportListType.BOOKMARKS.toString())
}
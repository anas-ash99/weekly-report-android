package com.anas.weeklyreport.presentaion.home.report_list


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.anas.weeklyreport.AppData.appLanguage
import com.anas.weeklyreport.extension_methods.capitalizeFirstLetter
import com.anas.weeklyreport.extension_methods.translateListType
import com.anas.weeklyreport.presentaion.LoadingSpinner
import com.anas.weeklyreport.screen_actions.HomeScreenEvent
import com.anas.weeklyreport.shared.AppColors
import com.anas.weeklyreport.shared.BottomSheetBodyType
import com.anas.weeklyreport.shared.ReportListType
import com.anas.weeklyreport.viewmodels.HomeViewmodel

@Composable
fun ReportListScreen(
    navController: NavController?,
    type:String
) {
    val viewmodel: HomeViewmodel = hiltViewModel()
    val state by viewmodel.state.collectAsState()
    val reports = viewmodel.reports
    LaunchedEffect(Unit){
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
            Divider(color = AppColors.DIVIDER.color, thickness = 1.dp, modifier = Modifier.padding(bottom = 15.dp))

            ReportList(
                state = state,
                onEvent = viewmodel::onEvent,
                reports = reports,
                type = type,
                navController = navController!!
            )
        }
        LoadingSpinner(isVisible = state.screenLoading)
    }

}
//val typeMap = mapOf("BOOKMARK" to "Bookmark", "TRASH" to "Trash", "")
@Preview
@Composable
fun ReportScreenListPreview(){
    ReportListScreen(null, ReportListType.BOOKMARKS.toString())
}
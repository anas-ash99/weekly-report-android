package com.anas.weeklyreport.presentaion.home.report_list

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.anas.weeklyreport.R
import com.anas.weeklyreport.data.HomeScreenState
import com.anas.weeklyreport.model.Report
import com.anas.weeklyreport.presentaion.home.ShimmerListItem
import com.anas.weeklyreport.presentaion.home.bottom_sheet.HomeBottomSheet
import com.anas.weeklyreport.screen_actions.HomeScreenEvent
import com.anas.weeklyreport.shared.AppScreen
import com.anas.weeklyreport.shared.ReportListType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportList(
    state: HomeScreenState,
    onEvent:(HomeScreenEvent) ->Unit,
    reports:MutableList<Report>,
    navController: NavController,
    type:String,
) {
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    LaunchedEffect(state.screen){ // launchedEffect for handling navigation
        if (state.screen == AppScreen.HOME_SCREEN.toString()){
            navController.popBackStack()
        }else if (state.screen.isNotBlank()){
            navController.navigate(state.screen)
            state.screen = ""
        }
    }
    LaunchedEffect(state.documentUri){ // open the downloaded document
        state.documentUri?.let {
            openWordDocument(context, it)
            state.documentUri = null
        }
    }
    LaunchedEffect(state.toastMessage){
        if (state.toastMessage.isNotBlank()){
//            Toast.makeText(context, state.toastMessage, Toast.LENGTH_SHORT).show()
            state.toastMessage = ""
        }
    }
    HomeBottomSheet(state = state, sheetState = sheetState,  onEvent = onEvent)

    val filteredReports = when(type){ // filter the reports before calling the lazy column for better spacing between items
        ReportListType.ALL.toString() -> reports.filter { !it.isInTrash }
        ReportListType.BOOKMARKS.toString() -> reports.filter { it.isBookmarked && !it.isInTrash }
        ReportListType.TRASH.toString() -> reports.filter { it.isInTrash }
        else -> reports
    }


    if (state.itemsLoading){
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)){
            items(20){
                ShimmerListItem()
            }
        }

    }else{
        LazyColumn(state = listState, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filteredReports) { item ->
                ReportListItem(item, onEvent)
            }
            item { Spacer(modifier = Modifier.height(35.dp)) } // add bottom padding to the last item
        }

    }


}

fun openWordDocument(context: Context, contentUri: Uri) {
    try {
        val intent = Intent(Intent.ACTION_VIEW)
            .setDataAndType(contentUri, "application/msword")
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
        context.startActivity(intent)
    }catch (e:Exception){
        Log.e("open document", e.message, e)
        Toast.makeText(context, context.getText(R.string.no_App_found_error), Toast.LENGTH_SHORT).show()
    }

}
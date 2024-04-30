package com.anas.weeklyreport.presentaion.home.report_list

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReportList(
    state: HomeScreenState,
    onEvent:(HomeScreenEvent) ->Unit,
    reports:MutableList<Report>,
    type:String,
) {
    val listState = rememberLazyListState()

    if (state.itemsLoading){
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)){
            items(20){
                ShimmerListItem()
            }
        }
    }else{
        LazyColumn(state = listState) {
            items(reports.sortedByDescending { it.createdAt }) { item ->
                when(type){ // filter the reports before calling the lazy column for better spacing between items
                    ReportListType.ALL.toString() -> ReportListItem(Modifier.animateItemPlacement(), item, onEvent, !item.isInTrash )
                    ReportListType.BOOKMARKS.toString() -> ReportListItem(Modifier.animateItemPlacement(), item, onEvent, item.isBookmarked && !item.isInTrash )
                    ReportListType.TRASH.toString() -> ReportListItem(Modifier.animateItemPlacement(), item, onEvent, item.isInTrash && !item.isDeleted)
                }

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
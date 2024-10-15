package com.anas.weeklyreport.presentaion.home.report_list

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.anas.weeklyreport.R
import com.anas.weeklyreport.data.HomeScreenState
import com.anas.weeklyreport.extension_methods.convertStringToDate
import com.anas.weeklyreport.extension_methods.getYear
import com.anas.weeklyreport.extension_methods.shimmerEffect
import com.anas.weeklyreport.model.Report
import com.anas.weeklyreport.presentaion.home.ShimmerListItem
import com.anas.weeklyreport.screen_actions.HomeScreenEvent
import com.anas.weeklyreport.shared.AppColors
import com.anas.weeklyreport.shared.ReportListType

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReportList(
    state: HomeScreenState,
    onEvent:(HomeScreenEvent) ->Unit,
    reports:MutableList<Report>,
    type:String,
    filterCategories:List<Int> = arrayListOf()
) {
    val listState = rememberLazyListState()

        LazyColumn(state = listState) {

            val sortedItems = reports.sortedByDescending { it.fromDate.convertStringToDate("dd/MM/yyyy") }

            item {
                if (filterCategories.isNotEmpty()){
                    FilterLabels(state = state, onEvent = onEvent, filterCategories =filterCategories, loading = state.itemsLoading)
                }
            } // add bottom padding to the last item
            if (state.itemsLoading){
                items(15){
                    ShimmerListItem()
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }else{

                items(sortedItems) { item ->

                    val isVisible  =  when(type){ // filter the reports before calling the lazy column for better spacing between items
                        ReportListType.ALL.toString() -> {
                            if (state.filterReportsBy == R.string.all){
                                !item.isInTrash
                            }else{
                                !item.isInTrash && item.fromDate.getYear() == state.filterReportsBy.toString()
                            }
                        }
                        ReportListType.BOOKMARKS.toString() -> {
                            item.isBookmarked && !item.isInTrash
                        }
                        ReportListType.TRASH.toString() -> {
                            item.isInTrash && !item.isDeleted
                        }
                        else -> {false}
                    }
                    ReportListItem(
                        Modifier.animateItemPlacement(),
                        item,
                        onEvent,
                        isVisible,
                    )

                }
            }
            item { Spacer(modifier = Modifier.height(35.dp)) } // add bottom padding to the last item
        }
}


@Composable
fun FilterLabels(
    state:HomeScreenState,
    onEvent: (HomeScreenEvent) -> Unit,
    filterCategories:List<Int>,
    loading:Boolean
) {
    LazyRow (
        Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .height(55.dp)
            .verticalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment= Alignment.CenterVertically,
    ){
        item{
            Spacer(modifier = Modifier.width(10.dp))
        }
       if (loading){
           items(5){ index ->
               if (index != 0) Spacer(modifier = Modifier.width(5.dp))
               Box(
                   modifier = Modifier
                       .height(40.dp)
                       .width(75.dp)
                       .clip(RoundedCornerShape(20.dp))
                       .shimmerEffect(),
               )
           }
       }else{
           itemsIndexed(filterCategories){ index, label ->
               if (index != 0) Spacer(modifier = Modifier.width(5.dp))
               Button(
                   colors = ButtonDefaults.buttonColors(
                       contentColor =if(label == state.filterReportsBy ) Color.White else Color.Unspecified,
                       containerColor = if(label == state.filterReportsBy) AppColors.APP_MAIN_COLOR else Color.LightGray,
                   ),
                   onClick = { onEvent(HomeScreenEvent.FilterReports(label))}
               ) {
                   Text(text = if (index == 0) stringResource(id = label) else label.toString())
               }
           }
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
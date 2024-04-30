package com.anas.weeklyreport.presentaion.home.bottom_sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.anas.weeklyreport.R
import com.anas.weeklyreport.data.HomeScreenState
import com.anas.weeklyreport.screen_actions.HomeScreenEvent

@Composable
fun AllOptionsSheetBody(

    onEvent:(HomeScreenEvent)->Unit,
    state:HomeScreenState,

) {
    Column {

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black
                ),
                onClick = {
                    onEvent(HomeScreenEvent.OnDownloadReport)
                    onEvent(HomeScreenEvent.OnDismissBottomSheet)
                }
            ) {
                Row (
                    modifier= Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment= Alignment.CenterVertically,
                ){

                    Icon(painter = painterResource(id = R.drawable.outline_download_24), contentDescription =  stringResource(id = R.string.download), modifier = Modifier.size(27.dp))
                    Text(text = stringResource(id = R.string.download),style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 10.dp))
                }
            }
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black
                ),
                onClick = {
                    onEvent(HomeScreenEvent.OnDocumentItemClick(state.currentReport.id))
                    onEvent(HomeScreenEvent.OnDismissBottomSheet)
                }
            ) {
                Row (
                    modifier= Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment= Alignment.CenterVertically,
                ){

                    Icon(imageVector = Icons.Outlined.Edit, contentDescription = stringResource(id = R.string.edit), modifier = Modifier.size(25.dp))
                    Text(text = stringResource(id = R.string.edit),style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 10.dp))
                }
            }
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black
                ),
                onClick = {
                    onEvent(HomeScreenEvent.OnBookmarkReport)
                    onEvent(HomeScreenEvent.OnDismissBottomSheet)
                }
            ) {
                Row (
                    modifier= Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment= Alignment.CenterVertically,
                ){
                    val icon = if (state.currentReport.isBookmarked) painterResource(id = R.drawable.bookmarked_icon) else painterResource(id = R.drawable.outline_bookmark_border_24)
                    val text = if (state.currentReport.isBookmarked) stringResource(id = R.string.remove_from_bookmark) else stringResource(id = R.string.bookmark)
                    Icon(painter = icon, contentDescription = stringResource(id = R.string.bookmark))
                    Text(text = text,style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 10.dp))
                }
            }
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black
                ),
                onClick = {
                    onEvent(HomeScreenEvent.OnMoveToTrashClick)
                    onEvent(HomeScreenEvent.OnDismissBottomSheet)
                }
            ) {
                Row (
                    modifier= Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment= Alignment.CenterVertically,
                ){
                    Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete")
                    Text(text = stringResource(id = R.string.move_to_trash),style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 10.dp))
                }
            }

        }

}
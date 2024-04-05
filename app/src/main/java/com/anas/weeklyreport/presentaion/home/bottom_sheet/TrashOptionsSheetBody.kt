package com.anas.weeklyreport.presentaion.home.bottom_sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.anas.weeklyreport.R
import com.anas.weeklyreport.screen_actions.HomeScreenEvent

@Composable
fun TrashOptionsSheetBody(
    onEvent:(HomeScreenEvent)->Unit
) {
    Column {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black
                ),
                onClick = {
                    onEvent(HomeScreenEvent.OnRestoreReportClick)
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

                    Icon(imageVector = Icons.Default.Refresh, contentDescription = stringResource(id = R.string.restore), modifier = Modifier.size(27.dp))
                    Text(text = stringResource(id = R.string.restore),style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 10.dp))
                }
            }

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black
                ),
                onClick = {
                    onEvent(HomeScreenEvent.OnDeleteReportClick)
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

                    Icon(imageVector = Icons.Outlined.Delete, contentDescription = "delete", modifier = Modifier.size(27.dp))
                    Text(text = stringResource(id = R.string.delete),style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 10.dp))
                }
            }

    }
}
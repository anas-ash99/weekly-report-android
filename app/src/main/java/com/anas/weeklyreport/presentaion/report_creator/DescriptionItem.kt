package com.anas.weeklyreport.presentaion.report_creator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.anas.weeklyreport.R
import com.anas.weeklyreport.model.Description
import com.anas.weeklyreport.screen_actions.ReportCreatorScreenEvent

@Composable
fun DescriptionItem(
    description: Description,
    index: Int,
    day:String,
    onEvent: (ReportCreatorScreenEvent) -> Unit
) {
    var contextMenuExpanded by remember { mutableStateOf(false) }
    Column {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment= Alignment.CenterVertically
        ){
            Text(text ="- ${description.description}", modifier = Modifier.weight(1f), maxLines = 1,
                overflow = TextOverflow.Ellipsis)
            Text(text = "${description.hours} hours", modifier = Modifier.padding(start = 5.dp))
            IconButton(onClick = {
                contextMenuExpanded = true

            }) {
                Icon(Icons.Filled.MoreVert, contentDescription = stringResource(id = R.string.more_options))
            }

        }
        MyContextMenu(
            contextMenuExpanded = contextMenuExpanded,
            onDismissRequest = {
                contextMenuExpanded = false
            },
            onEdit = {
                contextMenuExpanded = false
                onEvent(ReportCreatorScreenEvent.OnEditDescriptionClick(description, day, index))
            },
            onDelete = {
                contextMenuExpanded = false
            })
    }
}


@Composable
fun MyContextMenu(
    contextMenuExpanded:Boolean,
    onDismissRequest:()->Unit,
    onEdit:()->Unit,
    onDelete:() ->Unit
) {
    DropdownMenu(
        expanded = contextMenuExpanded,
        onDismissRequest = onDismissRequest,
        offset = DpOffset(x = LocalConfiguration.current.screenWidthDp.dp - 160.dp, y = (-10).dp)
    ) {
        DropdownMenuItem(text = { Text(text = "Edit") }, onClick = onEdit)
        DropdownMenuItem(text = { Text(text = "Delete") }, onClick = onDelete)
    }
}
package com.anas.weeklyreport.presentaion.home.report_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anas.weeklyreport.R
import com.anas.weeklyreport.model.Report
import com.anas.weeklyreport.screen_actions.HomeScreenEvent
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun ReportListItem(document: Report, onEvent:(HomeScreenEvent) ->Unit = {}) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        onClick = {onEvent(HomeScreenEvent.OnDocumentItemClick(document.id)) },
        contentPadding = PaddingValues(vertical = 2.dp, horizontal = 5.dp),
        shape = RoundedCornerShape(15.dp),
        colors = ButtonDefaults.buttonColors(contentColor = Color.Black, containerColor = Color(0xFFebedf1))
    ) {
        Row (
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement= Arrangement.SpaceBetween,
            verticalAlignment= Alignment.CenterVertically,
        ){
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = document.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "${stringResource(id = R.string.number)}: ${document.reportNumber}",
                    color = Color(0xB9242424),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "${stringResource(id = R.string.date)}: ${formatDateTime( document.createdAt)}",
                    color = Color(0xB9242424),
                )
            }
            IconButton(onClick = {  onEvent(HomeScreenEvent.OnOpenBottomSheet(document))}) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = stringResource(id = R.string.more_options), modifier = Modifier.size(30.dp))
            }
        }
    }
}
fun formatDateTime(inputDateTime: String, pattern: String = "MMM d,yyyy"): String {
    val originalDateTime = LocalDateTime.parse(inputDateTime)
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return originalDateTime.format(formatter)
}


@Preview
@Composable
fun ReportListItemPreview() {
   ReportListItem(document = Report())
}


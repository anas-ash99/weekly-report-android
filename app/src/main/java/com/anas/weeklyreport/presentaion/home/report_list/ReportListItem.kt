package com.anas.weeklyreport.presentaion.home.report_list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import com.anas.weeklyreport.extension_methods.getYear
import com.anas.weeklyreport.model.Report
import com.anas.weeklyreport.screen_actions.HomeScreenEvent


@Composable
fun ReportListItem(modifier: Modifier = Modifier, report: Report, onEvent:(HomeScreenEvent) ->Unit, isVisible:Boolean) {


    AnimatedVisibility(
        visible = isVisible,
        exit = shrinkVertically (
            animationSpec = tween(500),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {

        Column {
            Button(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp, start = 8.dp, end = 8.dp),
                onClick = {
                    if (!report.isInTrash) {
                        onEvent(HomeScreenEvent.OnDocumentItemClick(report.id))
                    }
                },
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
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "KW",
                            fontWeight = FontWeight.Normal,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = report.calenderWeak,
                            fontWeight = FontWeight.Medium,
                            fontSize = 30.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = report.fromDate.getYear(),
                            color = Color(0xB9242424),
                        )
                    }

                    Column (
//                    modifier = Modifier.fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Text(
                            text = report.fromDate,
                            color = Color(0xB9242424),
                            fontSize = 16.sp
                        )
                        Text(
                            text = " - ",
                            color = Color(0xB9242424),
                            fontSize = 16.sp
                        )
                        Text(
                            text = report.toDate,
                            color = Color(0xB9242424),
                            fontSize = 16.sp
                        )

                    }
                    IconButton(onClick = {  onEvent(HomeScreenEvent.OnReportItemMoreActionClick(report))}) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = stringResource(id = R.string.more_options), modifier = Modifier.size(30.dp))
                    }
                }
            }

        }
    }
}

@Preview
@Composable
fun ReportListItemPreview() {
   ReportListItem(report = Report(), isVisible = true, onEvent = {})
}


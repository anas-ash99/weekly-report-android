package com.anas.weeklyreport.presentaion.report_creator

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anas.weeklyreport.AppData
import com.anas.weeklyreport.R
import com.anas.weeklyreport.domain.ReportCreatorScreenEvent
import com.anas.weeklyreport.domain.ReportCreatorScreenStates
import com.anas.weeklyreport.extension_methods.capitalizeFirstLetter
import com.anas.weeklyreport.extension_methods.translateDay
import com.anas.weeklyreport.model.Weekday
import com.anas.weeklyreport.shared.AppColors

@SuppressLint("SuspiciousIndentation")
@Composable
fun WeekdayItem(
    day:String,
    data: Weekday,
    state: ReportCreatorScreenStates,
    onEvent:(ReportCreatorScreenEvent) -> Unit
) {

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        val dayText = if (AppData.appLanguage == "de") data.day.translateDay().capitalizeFirstLetter() else data.day.capitalizeFirstLetter()
        Text(text = dayText, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 5.dp))

        data.descriptions.forEachIndexed { index, des ->
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment= Alignment.CenterVertically
            ){
                Text(text ="- ${des.description}", modifier = Modifier.weight(1f), maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
                Text(text = "${des.hours} hours", modifier = Modifier.padding(start = 5.dp))
                IconButton(onClick = {
                    state.currentWeekDayItem = day
                    state.isTypeAdd = false
                    state.descriptionItemIndex = index
                    onEvent(ReportCreatorScreenEvent.OnEditDescriptionClick(des))
                }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = stringResource(id = R.string.more_options))
                }
            }
        }
        Text(text = "${state.weekDayListTrigger}", fontSize = 0.sp ) // invisible text to trigger a change in the item list

        Button(
            onClick = {
                state.currentWeekDayItem = day
                state.isTypeAdd = true
                onEvent(ReportCreatorScreenEvent.OnAddDescriptionClick)
            },
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.APP_MAIN_COLOR.color),
            modifier = Modifier.padding(top = 5.dp)

        ) {
            Text(stringResource(id = R.string.add_task_button))
        }
    }
}
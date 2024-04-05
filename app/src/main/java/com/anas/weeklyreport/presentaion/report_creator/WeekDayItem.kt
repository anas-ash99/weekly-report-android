package com.anas.weeklyreport.presentaion.report_creator

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anas.weeklyreport.AppData
import com.anas.weeklyreport.R
import com.anas.weeklyreport.screen_actions.ReportCreatorScreenEvent
import com.anas.weeklyreport.data.ReportCreatorScreenStates
import com.anas.weeklyreport.extension_methods.capitalizeFirstLetter
import com.anas.weeklyreport.extension_methods.translateDay
import com.anas.weeklyreport.model.Weekday
import com.anas.weeklyreport.shared.AppColors


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


        data.descriptions.forEachIndexed {index, des ->
          DescriptionItem(description = des, index, data.day,  onEvent)
        }
        Text(text = "${state.weekDayListTrigger}", fontSize = 0.sp ) // invisible text to trigger a change in the item list

        Button(
            onClick = {
                onEvent(ReportCreatorScreenEvent.OnAddDescriptionClick(day))
            },
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.APP_MAIN_COLOR.color),
            modifier = Modifier.padding(top = 5.dp)

        ) {
            Text(stringResource(id = R.string.add_task_button))
        }

    }
}



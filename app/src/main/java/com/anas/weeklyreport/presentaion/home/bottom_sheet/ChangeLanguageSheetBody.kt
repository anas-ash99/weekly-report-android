package com.anas.weeklyreport.presentaion.home.bottom_sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anas.weeklyreport.AppData
import com.anas.weeklyreport.R
import com.anas.weeklyreport.screen_actions.HomeScreenEvent
import com.anas.weeklyreport.shared.AppColors


@Composable
fun ChangeLanguageSheetBody(
    onEvent:(HomeScreenEvent) ->Unit,
) {
    var languages = listOf(
        Language(
            name = stringResource(id = R.string.english),
            code = "en",
            isSelected = AppData.appLanguage == "en"
        ),
        Language(
            name = stringResource(id = R.string.german),
            code = "de",
            isSelected = AppData.appLanguage == "de"
        ))
    Column(
        modifier = Modifier.padding(bottom = 20.dp)
    ){
        Text(text = stringResource(id = R.string.language_dialog_title), fontWeight = FontWeight.Bold, fontSize = 22.sp, modifier = Modifier.padding(bottom = 20.dp, start = 25.dp))
        Divider(color = AppColors.DIVIDER.color)

        if (AppData.appLanguage == "de"){
             languages = languages.reversed()
        }

        languages.forEach { item ->
            TextButton(onClick = {
                onEvent(HomeScreenEvent.OnLanguageSelection(item.code))
            }) {
                Row(
                    modifier = Modifier.padding(start = 10.dp),
                    verticalAlignment= Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    RadioButton(selected = item.isSelected , onClick = {    onEvent(HomeScreenEvent.OnLanguageSelection(item.code)) }, colors = RadioButtonDefaults.colors(selectedColor = AppColors.APP_MAIN_COLOR.color))
                    Text(
                        text = item.name,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 20.dp),
                        color = Color(0xFF444444),
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}
data class Language(
    val name:String,
    val code:String,
    val isSelected:Boolean
)
@Preview
@Composable
fun LanguageBodyPreview() {
    ChangeLanguageSheetBody(onEvent = {})
}
package com.anas.weeklyreport.presentaion.report_creator

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.anas.weeklyreport.screen_actions.ReportCreatorScreenEvent

@Composable
fun DateTextField(
    label:String,
    onEvent:(ReportCreatorScreenEvent) ->Unit,
    name:String,
    text:String
)
{
    TextField(
        value = text,
        onValueChange = {  },
        label = { Text(label) },
        readOnly = true,
        singleLine = true,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xBCC7D3DA),
            focusedContainerColor = Color(0xBCC7D3DA),
            unfocusedIndicatorColor = Color.Gray,
            focusedIndicatorColor = Color.Gray,
            cursorColor=  Color.Gray,
            focusedLabelColor= Color(0xFF326d8b),
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 15.dp)
            .onFocusChanged { focusState ->
                onEvent(ReportCreatorScreenEvent.RequestDatePicker(name, focusState.isFocused))
            }

    )

}


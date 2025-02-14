package com.anas.weeklyreport.presentaion.report_creator

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.anas.weeklyreport.screen_actions.ReportCreatorScreenEvent

@Composable
fun CustomStyledTextField(
    onEvent: (ReportCreatorScreenEvent) -> Unit,
    text:String,
    label:String,
    name:String,
    keyboardType: KeyboardType = KeyboardType.Number
){
    val context = LocalContext.current
    TextField(
        value = text,
        onValueChange = { onEvent(ReportCreatorScreenEvent.OnTextFieldValueChange(it,name)) },
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
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
            .padding(bottom = 10.dp, top = 10.dp)
    )
}
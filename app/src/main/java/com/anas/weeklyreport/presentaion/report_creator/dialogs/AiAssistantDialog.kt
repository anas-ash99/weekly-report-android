package com.anas.weeklyreport.presentaion.report_creator.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anas.weeklyreport.R
import com.anas.weeklyreport.screen_actions.ReportCreatorScreenEvent
import com.anas.weeklyreport.data.ReportCreatorScreenStates
import com.anas.weeklyreport.shared.AppColors

@Composable
fun AiAssistantDialog(
    state:ReportCreatorScreenStates,
    onEvent:(ReportCreatorScreenEvent) ->Unit,
) {
    if (state.isAiDialogShown) {
        AlertDialog(
            onDismissRequest = {
                onEvent(ReportCreatorScreenEvent.OnAiDialogRequest(false))
            },
            title = { Text(stringResource(id = R.string.ai_dailog_title)) },
            text = {
                Column {
                    Text(stringResource(id = R.string.ai_dailog_secondry_title))
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = state.promptText,
                        onValueChange = {  onEvent(ReportCreatorScreenEvent.OnPromptTextChange(it)) },
                        label = { Text(stringResource(id = R.string.ai_dailog_textfield_lable)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onEvent(ReportCreatorScreenEvent.OnGeneraAiResponseClick)
                        onEvent(ReportCreatorScreenEvent.OnAiDialogRequest(false))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.APP_MAIN_COLOR.color),
                    enabled =  state.promptText.isNotBlank()
                ) {
                    Text(stringResource(id = R.string.generate))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                      onEvent(ReportCreatorScreenEvent.OnAiDialogRequest(false))
                              },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.CANCEL_BUTTON_COLOR.color),
                ) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }
}


@Preview
@Composable
fun AiDialogPreview() {
    AiAssistantDialog( state = ReportCreatorScreenStates(isAiDialogShown = true) , {} )
}
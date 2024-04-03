package com.anas.weeklyreport.presentaion.report_creator.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.anas.weeklyreport.R
import com.anas.weeklyreport.domain.ReportCreatorScreenEvent
import com.anas.weeklyreport.domain.ReportCreatorScreenStates
import com.anas.weeklyreport.model.Description
import com.anas.weeklyreport.shared.AppColors

@Composable
fun AddDescriptionDialog(
    state: ReportCreatorScreenStates,
    onEvent:(ReportCreatorScreenEvent) ->Unit
) {
    if (state.isAddDescriptionDialogShown) {
        AlertDialog(
            onDismissRequest = { onEvent(ReportCreatorScreenEvent.OnAddDialogDismiss) },
            title = { Text(stringResource(id = R.string.description_dialog_title)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = state.dialogDescriptionItem.description,
                        onValueChange = { onEvent(ReportCreatorScreenEvent.OnDescriptionDialogValueChange(it)) },
                        label = { Text(stringResource(id = R.string.description)) }
                    )

                    OutlinedTextField(
                        value = state.dialogDescriptionItem.hours,
                        onValueChange = { onEvent(ReportCreatorScreenEvent.OnHoursDialogValueChange(it)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text(stringResource(id = R.string.hours)) }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onEvent(ReportCreatorScreenEvent.OnSaveDescriptionClick(Description(state.dialogDescriptionItem.description, state.dialogDescriptionItem.hours)))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.APP_MAIN_COLOR.color),
                    enabled = state.isSaveButtonInAddDescriptionDialogShown
                ) {
                    Text(stringResource(id = R.string.save))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        onEvent(ReportCreatorScreenEvent.OnAddDialogDismiss)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.CANCEL_BUTTON_COLOR.color),
                ) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }
}
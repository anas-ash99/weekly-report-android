package com.anas.weeklyreport.presentaion.settings


import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    modifier:Modifier = Modifier,
    onDismissRequest:() ->Unit,
    isShown:Boolean,
    sheetState:SheetState,
    content: @Composable () ->Unit
) {
    if (isShown) {
        ModalBottomSheet(
            modifier = modifier,
            onDismissRequest = onDismissRequest,
            sheetState = sheetState
        ) {
           content()
        }
    }
}
package com.anas.weeklyreport.presentaion.home.bottom_sheet

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.anas.weeklyreport.data.HomeScreenState
import com.anas.weeklyreport.screen_actions.HomeScreenEvent
import com.anas.weeklyreport.shared.BottomSheetBodyType


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeBottomSheet (modifier: Modifier = Modifier, sheetState: SheetState, onEvent:(HomeScreenEvent)->Unit, state:HomeScreenState){

    if (state.isBottomSheetShown) {
        ModalBottomSheet(
            modifier = modifier,
            onDismissRequest = {
                onEvent(HomeScreenEvent.OnDismissBottomSheet)
            },
            sheetState = sheetState
        ) {

            when(state.bottomSheetType){
                BottomSheetBodyType.ALL_OPTIONS -> AllOptionsSheetBody(onEvent = onEvent, state = state)
                BottomSheetBodyType.TRASH_OPTIONS -> TrashOptionsSheetBody(onEvent = onEvent)
                BottomSheetBodyType.CHANGE_LANGUAGE -> ChangeLanguageSheetBody(onEvent)
            }

        }
    }



}
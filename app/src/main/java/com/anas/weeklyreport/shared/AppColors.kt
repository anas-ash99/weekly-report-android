package com.anas.weeklyreport.shared

import androidx.compose.ui.graphics.Color

enum class  AppColors(val color:Color){
    APP_MAIN_COLOR(Color(0xFF326d8b)),
    DIVIDER(Color(0xF000000)),
    CANCEL_BUTTON_COLOR(Color(0xA4FF0000))
}

sealed interface Test{
    data class AppMainColor(val color: Color = Color(0xFF326d8b)): Test
}

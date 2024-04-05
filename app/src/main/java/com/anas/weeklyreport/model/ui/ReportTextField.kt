package com.anas.weeklyreport.model.ui

import androidx.compose.ui.text.input.KeyboardType

data class ReportTextField(
    val value:String,
    val name:String,
    val label:Int,
    val type: KeyboardType = KeyboardType.Number,
    val isDate:Boolean = false
)

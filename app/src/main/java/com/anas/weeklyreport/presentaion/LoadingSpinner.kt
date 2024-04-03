package com.anas.weeklyreport.presentaion

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay


@Composable
fun LoadingSpinner(isVisible:Boolean) {
    if (!isVisible) return

    Column (
        modifier = Modifier.fillMaxSize().clickable {  }.background(Color(0x511D1D1D)),
        verticalArrangement= Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        CircularProgressIndicator(
            modifier = Modifier.width(64.dp),
            color = Color(0xFF424242),
        )
    }
}
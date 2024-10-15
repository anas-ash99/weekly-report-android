package com.anas.weeklyreport.presentaion.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.anas.weeklyreport.extension_methods.shimmerEffect

@Composable
fun ShimmerListItem(
    modifier:Modifier = Modifier
){

    Column {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .height(110.dp)
                .clip(RoundedCornerShape(15.dp))
                .shimmerEffect(),
        )

    }
}
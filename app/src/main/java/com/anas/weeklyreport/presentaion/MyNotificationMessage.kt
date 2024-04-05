package com.anas.weeklyreport.presentaion

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MyNotificationMessage(
    modifier: Modifier = Modifier,
    message: String,
    durationMillis: Int = 2000,
    isVisible: Boolean,
    onTimeout: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var offsetY by remember { mutableFloatStateOf(0f) }
    val animatedOffsetY by animateFloatAsState(
        targetValue = if (isVisible) 0f else -100f,
        animationSpec = tween(durationMillis / 3, easing = LinearOutSlowInEasing),
        label = ""
    )

    LaunchedEffect(isVisible) {
        if (isVisible) {
            offsetY = -100f
            delay(100)
            offsetY = 0f
            scope.launch { // after durationMillis call the timeout function
                delay(durationMillis.toLong())
                onTimeout()
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .offset(y = animatedOffsetY.dp), // Use animated offset
    ) {
        Text(
            text = message,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview
@Composable
fun MyNotificationMessagePreview() {
    MyNotificationMessage(message = "Item permanently deleted", isVisible = true)
}
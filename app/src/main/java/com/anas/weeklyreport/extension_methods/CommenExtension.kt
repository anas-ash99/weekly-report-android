package com.anas.weeklyreport.extension_methods

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import com.anas.weeklyreport.AppData
import com.anas.weeklyreport.model.Report
import kotlinx.coroutines.flow.update
import java.util.Locale

fun List<Report>.updateItemById(report: Report):Boolean {
    val updatedList = this.toMutableList() // Create a mutable copy to avoid ConcurrentModificationException
    var isUpdated = false
    try {
        updatedList.forEachIndexed{index, doc->
            if (doc.id == report.id){
                updatedList[index] = report
                isUpdated = true
            }
        }

    }catch (e:Exception){
        isUpdated = false
//        state.update { body ->
//            body.copy(
//                toastMessage = "Unknown error occurred"
//            )
//        }
    }
    return isUpdated
}



fun Context.myUpdateConfiguration(locale: Locale): Configuration {
    return resources.configuration.apply { setLocale(locale) }
}

fun Context.setLocale(languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val resources = this.resources
    val config: Configuration = resources.configuration
    config.setLocale(locale)
    resources.updateConfiguration(config, resources.displayMetrics)
}

fun Context.updateLocale(locale: Locale): Context {
    Locale.setDefault(locale) // Set the default locale (affects system-wide if possible)
    val configuration = resources.configuration.apply { setLocale(locale) }
    return createConfigurationContext(configuration)
}


fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition(label = "")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000)
        ), label = ""
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFB8B5B5),
                Color(0xFF8F8B8B),
                Color(0xFFB8B5B5),
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    )
        .onGloballyPositioned {
            size = it.size
        }
}
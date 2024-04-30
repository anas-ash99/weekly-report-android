@file:Suppress("DEPRECATION")

package com.anas.weeklyreport.extension_methods


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
import com.anas.weeklyreport.database.entities.ReportEntity
import com.anas.weeklyreport.model.Report
import java.util.Locale

fun Context.setLocale(languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val resources = this.resources
    val config: Configuration = resources.configuration
    config.setLocale(locale)
    resources.updateConfiguration(config, resources.displayMetrics)
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

fun Report.toReportEntity(): ReportEntity {
    return ReportEntity(
        id = this.id,
        userId = this.userId,
        name = this.name,
        year = this.year,
        isSynced = this.isSynced,
        calenderWeak = this.calenderWeak,
        fromDate = this.fromDate,
        toDate = this.toDate,
        reportNumber = this.reportNumber,
        isInTrash = this.isInTrash,
        isDeleted = this.isDeleted,
        isBookmarked = this.isBookmarked,
        createdAt = this.createdAt,
    )
}

fun ReportEntity.toReport():Report{
    return Report(
        id = this.id,
        userId = this.userId,
        name = this.name,
        year = this.year,
        calenderWeak = this.calenderWeak,
        fromDate = this.fromDate,
        toDate = this.toDate,
        reportNumber = this.reportNumber,
        isInTrash = this.isInTrash,
        isSynced = this.isSynced,
        isDeleted = this.isDeleted,
        isBookmarked = this.isBookmarked,
        createdAt = this.createdAt
    )
}
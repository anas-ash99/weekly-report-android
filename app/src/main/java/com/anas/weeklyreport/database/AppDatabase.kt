package com.anas.weeklyreport.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.anas.weeklyreport.database.entities.DescriptionEntity
import com.anas.weeklyreport.database.entities.ReportEntity
import com.anas.weeklyreport.database.entities.WeekdayDescriptionEntity


@Database(
    entities = [ReportEntity::class, DescriptionEntity::class, WeekdayDescriptionEntity::class],
    version = 9
)
abstract class AppDatabase :RoomDatabase() {
    abstract fun reportDao():ReportDao
}
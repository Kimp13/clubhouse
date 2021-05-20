package com.example.presentation.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.presentation.data.daos.ContactLocationDao
import com.example.presentation.data.entities.SavedContactLocation

@Database(entities = [SavedContactLocation::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactLocationDao(): ContactLocationDao
}
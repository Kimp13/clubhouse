package com.example.clubhouse.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.clubhouse.data.daos.ContactLocationDao
import com.example.clubhouse.data.entities.ContactLocation

@Database(entities = [ContactLocation::class], version = 1)
abstract class ContactDatabase : RoomDatabase() {
    abstract fun contactLocationDao() : ContactLocationDao
}
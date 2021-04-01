package com.example.clubhouse.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.clubhouse.data.entities.ContactLocation

@Dao
interface ContactLocationDao {
    @Query("select * from contact_location where contact_id = :contactId")
    suspend fun findById(contactId: Long): ContactLocation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(contactLocation: ContactLocation)
}
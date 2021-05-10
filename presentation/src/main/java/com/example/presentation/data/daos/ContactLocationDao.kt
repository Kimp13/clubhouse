package com.example.presentation.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.presentation.data.entities.SavedContactLocation

@Dao
interface ContactLocationDao {
    @Query("select * from contact_location where contact_id = :contactId")
    suspend fun findById(contactId: Long): SavedContactLocation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(contactLocation: SavedContactLocation)
}
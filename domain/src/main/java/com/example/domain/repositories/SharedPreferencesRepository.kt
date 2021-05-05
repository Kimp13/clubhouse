package com.example.domain.repositories

interface SharedPreferencesRepository {
    suspend fun getBoolean(key: String): Boolean
    suspend fun writeBoolean(key: String, value: Boolean): Boolean
}
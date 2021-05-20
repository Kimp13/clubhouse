package com.example.domain.repositories.interfaces

interface BasicTypesRepository {
    suspend fun getBoolean(key: String): Boolean
    suspend fun writeBoolean(key: String, value: Boolean): Boolean

    suspend fun getString(key: String): String?
    suspend fun writeString(key: String, value: String): Boolean

    suspend fun remove(key: String): Boolean

    suspend fun getAll(): Map<String, *>
}
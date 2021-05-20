package com.example.domain.interactors.interfaces

interface BasicTypesInteractor {
    suspend fun getBoolean(key: String): Boolean
    suspend fun writeBoolean(key: String, value: Boolean): Boolean

    suspend fun getString(key: String): String?
    suspend fun writeString(key: String, value: String): Boolean

    suspend fun remove(key: String): Boolean

    suspend fun getAll(): MutableMap<String, *>
}
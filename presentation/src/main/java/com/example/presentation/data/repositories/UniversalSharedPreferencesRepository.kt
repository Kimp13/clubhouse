package com.example.presentation.data.repositories

import android.content.Context
import com.example.domain.repositories.interfaces.BasicTypesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val COMMON_SHARED_PREFERENCES_KEY = "common_shared_preferences"
const val CONTACT_SHARED_PREFERENCES_KEY = "contact_shared_preferences"

class UniversalSharedPreferencesRepository(
    context: Context,
    name: String
) : BasicTypesRepository {
    private val sharedPreferences = context.getSharedPreferences(
        name,
        Context.MODE_PRIVATE
    )

    override suspend fun getBoolean(key: String) = withContext(Dispatchers.IO) {
        sharedPreferences.getBoolean(key, false)
    }

    override suspend fun writeBoolean(key: String, value: Boolean) =
        withContext(Dispatchers.IO) {
            sharedPreferences.edit()
                .putBoolean(key, value)
                .commit()
        }

    override suspend fun getString(key: String) = withContext(Dispatchers.IO) {
        sharedPreferences.getString(key, null)
    }

    override suspend fun writeString(key: String, value: String) =
        withContext(Dispatchers.IO) {
            sharedPreferences.edit().putString(key, value).commit()
        }

    override suspend fun remove(key: String) = withContext(Dispatchers.IO) {
        sharedPreferences.edit().remove(key).commit()
    }

    override suspend fun getAll(): Map<String, *> =
        withContext(Dispatchers.IO) {
            sharedPreferences.all
        }
}
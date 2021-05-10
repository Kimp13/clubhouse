package com.example.presentation.data.repositories

import android.content.Context
import com.example.domain.repositories.BasicTypesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val COMMON_SHARED_PREFERENCES_KEY = "common_shared_prefs"

class CommonSharedPreferencesRepository(
    context: Context
) : BasicTypesRepository {
    private val sharedPreferences = context.getSharedPreferences(
        COMMON_SHARED_PREFERENCES_KEY,
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
}
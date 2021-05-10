package com.example.clubhouse.modules

import android.content.Context
import com.example.domain.repositories.ContactRepository
import com.example.domain.repositories.LastLocationRepository
import com.example.domain.repositories.SharedPreferencesRepository
import com.example.presentation.data.repositories.CommonSharedPreferencesRepository
import com.example.presentation.data.repositories.ContactProviderRepository
import com.example.presentation.data.repositories.FusedClientLocationRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Provides
    @Singleton
    fun provideContactRepository(
        context: Context
    ): ContactRepository {
        return ContactProviderRepository(context)
    }

    @Provides
    @Singleton
    fun provideLastLocationRepository(
        context: Context
    ): LastLocationRepository {
        return FusedClientLocationRepository(context)
    }

    @Provides
    @Singleton
    fun provideSharedPreferencesRepository(
        context: Context
    ): SharedPreferencesRepository {
        return CommonSharedPreferencesRepository(context)
    }
}
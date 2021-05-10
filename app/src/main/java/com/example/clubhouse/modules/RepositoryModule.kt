package com.example.clubhouse.modules

import android.content.Context
import com.example.domain.repositories.BasicTypesRepository
import com.example.domain.repositories.ContactRepository
import com.example.domain.repositories.LocationRepository
import com.example.presentation.data.daos.ContactLocationDao
import com.example.presentation.data.repositories.CommonSharedPreferencesRepository
import com.example.presentation.data.repositories.ContactProviderRepository
import com.example.presentation.data.repositories.LocationRepositoryImpl
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
    fun provideBasicTypesRepository(
        context: Context
    ): BasicTypesRepository {
        return CommonSharedPreferencesRepository(context)
    }

    @Provides
    @Singleton
    fun provideLocationRepository(
        context: Context,
        contactLocationDao: ContactLocationDao
    ): LocationRepository {
        return LocationRepositoryImpl(context, contactLocationDao)
    }
}
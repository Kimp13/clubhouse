package com.example.clubhouse.modules

import android.content.Context
import com.example.clubhouse.qualifiers.CommonSharedPreferences
import com.example.clubhouse.qualifiers.ContactSharedPreferences
import com.example.domain.repositories.implementations.DateTimeRepositoryImpl
import com.example.domain.repositories.interfaces.BasicTypesRepository
import com.example.domain.repositories.interfaces.ContactRepository
import com.example.domain.repositories.interfaces.DateTimeRepository
import com.example.domain.repositories.interfaces.GeocodingRepository
import com.example.domain.repositories.interfaces.LocationRepository
import com.example.domain.repositories.interfaces.ReminderRepository
import com.example.presentation.data.apis.DirectionsApi
import com.example.presentation.data.apis.GeocodingApi
import com.example.presentation.data.daos.ContactLocationDao
import com.example.presentation.data.repositories.AlarmRepository
import com.example.presentation.data.repositories.COMMON_SHARED_PREFERENCES_KEY
import com.example.presentation.data.repositories.CONTACT_SHARED_PREFERENCES_KEY
import com.example.presentation.data.repositories.ContactProviderRepository
import com.example.presentation.data.repositories.GoogleGeocodingRepository
import com.example.presentation.data.repositories.LocationRepositoryImpl
import com.example.presentation.data.repositories.UniversalSharedPreferencesRepository
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
    @ContactSharedPreferences
    fun provideContactSharedPreferencesRepository(
        context: Context
    ): BasicTypesRepository {
        return UniversalSharedPreferencesRepository(
            context,
            CONTACT_SHARED_PREFERENCES_KEY
        )
    }

    @Provides
    @Singleton
    @CommonSharedPreferences
    fun provideCommonSharedPreferencesRepository(
        context: Context
    ): BasicTypesRepository = UniversalSharedPreferencesRepository(
        context,
        COMMON_SHARED_PREFERENCES_KEY
    )

    @Provides
    @Singleton
    fun provideReminderRepository(
        context: Context
    ): ReminderRepository = AlarmRepository(context)

    @Provides
    @Singleton
    fun provideDateTimeRepository(): DateTimeRepository =
        DateTimeRepositoryImpl()

    @Provides
    @Singleton
    fun provideLocationRepository(
        context: Context,
        contactLocationDao: ContactLocationDao,
        directionsApi: DirectionsApi
    ): LocationRepository {
        return LocationRepositoryImpl(
            context,
            contactLocationDao,
            directionsApi
        )
    }

    @Provides
    @Singleton
    fun provideGeocodingRepository(
        api: GeocodingApi
    ): GeocodingRepository = GoogleGeocodingRepository(api)
}

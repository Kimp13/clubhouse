package com.example.clubhouse.modules

import android.content.Context
import com.example.domain.repositories.ContactRepository
import com.example.presentation.repositories.ContactProviderRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ContactRepositoryModule {
    @Provides
    @Singleton
    fun provideContactRepository(
        context: Context
    ): ContactRepository {
        return ContactProviderRepository(context)
    }
}
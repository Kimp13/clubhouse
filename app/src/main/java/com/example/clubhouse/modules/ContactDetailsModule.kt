package com.example.clubhouse.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.clubhouse.factories.AppViewModelFactory
import com.example.clubhouse.keys.ViewModelKey
import com.example.clubhouse.qualifiers.ContactSharedPreferences
import com.example.clubhouse.scopes.ContactDetailsFragmentScope
import com.example.domain.interactors.implementations.ContactDetailsAndReminderInteractor
import com.example.domain.repositories.interfaces.BasicTypesRepository
import com.example.domain.repositories.interfaces.ContactRepository
import com.example.domain.repositories.interfaces.DateTimeRepository
import com.example.domain.repositories.interfaces.ReminderRepository
import com.example.domain.repositories.LocationRepository
import com.example.presentation.ui.viewmodels.ContactDetailsViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
class ContactDetailsModule {
    @Provides
    @IntoMap
    @ViewModelKey(ContactDetailsViewModel::class)
    fun provideContactDetailsViewModel(
        interactor: ContactDetailsAndReminderInteractor
    ): ViewModel {
        return ContactDetailsViewModel(interactor)
    }

    @Provides
    @ContactDetailsFragmentScope
    fun provideContactDetailsAndReminderInteractor(
        contactRepository: ContactRepository,
        reminderRepository: ReminderRepository,
        dateTimeRepository: DateTimeRepository,

        @ContactSharedPreferences
        basicTypesRepository: BasicTypesRepository,
        locationRepository: LocationRepository
    ): ContactDetailsAndReminderInteractor {
        return ContactDetailsAndReminderInteractor(
            contactRepository,
            reminderRepository,
            dateTimeRepository,
            basicTypesRepository,
            locationRepository
        )
    }

    @Provides
    @ContactDetailsFragmentScope
    fun bindViewModelFactory(
        viewModelFactory: AppViewModelFactory
    ): ViewModelProvider.Factory {
        return viewModelFactory
    }
}
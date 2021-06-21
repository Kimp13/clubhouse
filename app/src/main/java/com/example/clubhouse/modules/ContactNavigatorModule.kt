package com.example.clubhouse.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.clubhouse.factories.AppViewModelFactory
import com.example.clubhouse.keys.ViewModelKey
import com.example.clubhouse.scopes.ContactNavigatorScope
import com.example.domain.interactors.implementations.ContactNavigatorInteractor
import com.example.domain.repositories.interfaces.LocationRepository
import com.example.presentation.ui.viewmodels.ContactNavigatorViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
class ContactNavigatorModule {
    @Provides
    @IntoMap
    @ViewModelKey(ContactNavigatorViewModel::class)
    fun provideContactNavigatorViewModel(
        navigatorInteractor: ContactNavigatorInteractor
    ): ViewModel = ContactNavigatorViewModel(navigatorInteractor)

    @Provides
    @ContactNavigatorScope
    fun provideNavigatorInteractor(
        locationRepository: LocationRepository
    ): ContactNavigatorInteractor {
        return ContactNavigatorInteractor(locationRepository)
    }

    @Provides
    @ContactNavigatorScope
    fun bindViewModelFactory(
        viewModelFactory: AppViewModelFactory
    ): ViewModelProvider.Factory = viewModelFactory
}

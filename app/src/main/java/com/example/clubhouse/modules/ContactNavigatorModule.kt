package com.example.clubhouse.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.clubhouse.factories.AppViewModelFactory
import com.example.clubhouse.keys.ViewModelKey
import com.example.clubhouse.scopes.ContactNavigatorScope
import com.example.domain.interactors.implementations.ContactNavigatorInteractor
import com.example.domain.repositories.LocationRepository
import com.example.domain.repositories.NavigatorRepository
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
        locationRepository: LocationRepository,
        navigatorRepository: NavigatorRepository
    ): ContactNavigatorInteractor {
        return ContactNavigatorInteractor(
            locationRepository,
            navigatorRepository
        )
    }

    @Provides
    @ContactNavigatorScope
    fun bindViewModelFactory(
        viewModelFactory: AppViewModelFactory
    ): ViewModelProvider.Factory = viewModelFactory
}
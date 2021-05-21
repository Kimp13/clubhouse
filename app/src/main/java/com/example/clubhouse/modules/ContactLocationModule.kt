package com.example.clubhouse.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.clubhouse.factories.AppViewModelFactory
import com.example.clubhouse.keys.ViewModelKey
import com.example.clubhouse.qualifiers.CommonSharedPreferences
import com.example.clubhouse.scopes.ContactLocationFragmentScope
import com.example.domain.interactors.implementations.ContactLocationInteractor
import com.example.domain.repositories.interfaces.BasicTypesRepository
import com.example.domain.repositories.interfaces.GeocodingRepository
import com.example.domain.repositories.interfaces.LocationRepository
import com.example.presentation.ui.viewmodels.ContactLocationViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
class ContactLocationModule {
    @Provides
    @IntoMap
    @ViewModelKey(ContactLocationViewModel::class)
    fun provideContactLocationViewModel(
        interactor: ContactLocationInteractor
    ): ViewModel {
        return ContactLocationViewModel(interactor)
    }

    @Provides
    @ContactLocationFragmentScope
    fun bindViewModelFactory(
        viewModelFactory: AppViewModelFactory
    ): ViewModelProvider.Factory {
        return viewModelFactory
    }

    @Provides
    @ContactLocationFragmentScope
    fun provideLocationInteractor(
        locationRepository: LocationRepository,
        geocodingRepository: GeocodingRepository,

        @CommonSharedPreferences
        basicTypesRepository: BasicTypesRepository
    ): ContactLocationInteractor {
        return ContactLocationInteractor(
            locationRepository,
            basicTypesRepository,
            geocodingRepository
        )
    }
}

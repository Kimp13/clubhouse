package com.example.clubhouse.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.clubhouse.factories.AppViewModelFactory
import com.example.clubhouse.keys.ViewModelKey
import com.example.clubhouse.scopes.ViewContactLocationFragmentScope
import com.example.domain.interactors.implementations.ViewContactLocationInteractorImpl
import com.example.domain.interactors.interfaces.ViewContactLocationInteractor
import com.example.domain.repositories.ContactRepository
import com.example.domain.repositories.LocationRepository
import com.example.presentation.ui.viewmodels.ViewContactLocationViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
class ViewContactLocationModule {
    @Provides
    @IntoMap
    @ViewModelKey(ViewContactLocationViewModel::class)
    fun provideViewContactLocationViewModel(
        interactor: ViewContactLocationInteractor
    ): ViewModel {
        return ViewContactLocationViewModel(interactor)
    }

    @Provides
    @ViewContactLocationFragmentScope
    fun bindViewModelFactory(
        viewModelFactory: AppViewModelFactory
    ): ViewModelProvider.Factory {
        return viewModelFactory
    }

    @Provides
    @ViewContactLocationFragmentScope
    fun provideIdContactListInteractor(
        contactRepository: ContactRepository,
        locationRepository: LocationRepository
    ): ViewContactLocationInteractor {
        return ViewContactLocationInteractorImpl(
            contactRepository,
            locationRepository
        )
    }
}
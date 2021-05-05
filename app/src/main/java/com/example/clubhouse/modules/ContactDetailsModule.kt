package com.example.clubhouse.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.clubhouse.factories.AppViewModelFactory
import com.example.clubhouse.keys.ViewModelKey
import com.example.clubhouse.scopes.ContactDetailsFragmentScope
import com.example.domain.interactors.implementations.ContactDetailsFragmentInteractor
import com.example.domain.interactors.interfaces.ContactDetailsInteractor
import com.example.domain.repositories.ContactRepository
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
        interactor: ContactDetailsInteractor
    ): ViewModel {
        return ContactDetailsViewModel(interactor)
    }

    @Provides
    @ContactDetailsFragmentScope
    fun provideContactDetailsInteractor(
        contactRepository: ContactRepository
    ): ContactDetailsInteractor {
        return ContactDetailsFragmentInteractor(contactRepository)
    }

    @Provides
    @ContactDetailsFragmentScope
    fun bindViewModelFactory(
        viewModelFactory: AppViewModelFactory
    ): ViewModelProvider.Factory {
        return viewModelFactory
    }
}
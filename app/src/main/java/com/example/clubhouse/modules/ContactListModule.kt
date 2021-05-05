package com.example.clubhouse.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.clubhouse.factories.AppViewModelFactory
import com.example.clubhouse.keys.ViewModelKey
import com.example.clubhouse.scopes.ContactListFragmentScope
import com.example.domain.interactors.implementations.ContactListFragmentInteractor
import com.example.domain.interactors.interfaces.SimpleContactListInteractor
import com.example.domain.repositories.ContactRepository
import com.example.presentation.viewmodels.ContactListViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
class ContactListModule {
    @Provides
    @ContactListFragmentScope
    fun provideSimpleContactListInteractor(
        contactRepository: ContactRepository
    ): SimpleContactListInteractor {
        return ContactListFragmentInteractor(contactRepository)
    }

    @Provides
    @IntoMap
    @ViewModelKey(ContactListViewModel::class)
    fun provideContactListViewModel(
        interactor: SimpleContactListInteractor
    ): ViewModel {
        return ContactListViewModel(interactor)
    }

    @Provides
    @ContactListFragmentScope
    fun bindViewModelFactory(
        viewModelFactory: AppViewModelFactory
    ): ViewModelProvider.Factory {
        return viewModelFactory
    }
}
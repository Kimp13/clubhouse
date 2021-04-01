package com.example.clubhouse.di.components

import com.example.clubhouse.di.scopes.ViewModelScope
import com.example.clubhouse.ui.viewmodels.ContactDetailsViewModel
import com.example.clubhouse.ui.viewmodels.ContactListViewModel
import dagger.Subcomponent

@Subcomponent
@ViewModelScope
interface ContactViewModelComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): ContactViewModelComponent
    }

    fun inject(viewModel: ContactDetailsViewModel)
    fun inject(viewModel: ContactListViewModel)
}
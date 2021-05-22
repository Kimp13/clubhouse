package com.example.clubhouse.components

import com.example.clubhouse.modules.ContactListModule
import com.example.clubhouse.scopes.ContactListFragmentScope
import com.example.clubhouse.scopes.ViewModelFactoryScope
import com.example.presentation.di.components.ContactListFragmentComponent
import dagger.Subcomponent

@Subcomponent(
    modules = [
        ContactListModule::class
    ]
)
@ContactListFragmentScope
@ViewModelFactoryScope
interface AnnotatedContactListFragmentComponent : ContactListFragmentComponent {
    @Subcomponent.Factory
    interface Factory : ContactListFragmentComponent.Factory {
        override fun create(): AnnotatedContactListFragmentComponent
    }
}

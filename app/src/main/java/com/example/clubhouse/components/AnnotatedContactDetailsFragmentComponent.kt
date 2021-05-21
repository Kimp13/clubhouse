package com.example.clubhouse.components

import com.example.clubhouse.modules.ContactDetailsModule
import com.example.clubhouse.scopes.ContactDetailsFragmentScope
import com.example.clubhouse.scopes.ViewModelFactoryScope
import com.example.presentation.di.components.ContactDetailsFragmentComponent
import dagger.Subcomponent

@Subcomponent(
    modules = [
        ContactDetailsModule::class
    ]
)
@ContactDetailsFragmentScope
@ViewModelFactoryScope
interface AnnotatedContactDetailsFragmentComponent :
    ContactDetailsFragmentComponent {
    @Subcomponent.Factory
    interface Factory : ContactDetailsFragmentComponent.Factory {
        override fun create(): AnnotatedContactDetailsFragmentComponent
    }
}

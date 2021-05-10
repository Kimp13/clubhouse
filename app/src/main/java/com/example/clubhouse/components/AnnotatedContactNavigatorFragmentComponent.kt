package com.example.clubhouse.components

import com.example.clubhouse.modules.ContactNavigatorModule
import com.example.clubhouse.scopes.ContactNavigatorScope
import com.example.presentation.di.components.ContactNavigatorFragmentComponent
import dagger.Subcomponent

@Subcomponent(modules = [ContactNavigatorModule::class])
@ContactNavigatorScope
interface AnnotatedContactNavigatorFragmentComponent :
    ContactNavigatorFragmentComponent {
    @Subcomponent.Factory
    interface Factory : ContactNavigatorFragmentComponent.Factory {
        override fun create(): AnnotatedContactNavigatorFragmentComponent
    }
}

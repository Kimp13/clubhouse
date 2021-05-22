package com.example.clubhouse.components

import com.example.clubhouse.modules.ContactLocationModule
import com.example.clubhouse.scopes.ContactLocationFragmentScope
import com.example.presentation.di.components.ContactLocationFragmentComponent
import dagger.Subcomponent

@Subcomponent(modules = [ContactLocationModule::class])
@ContactLocationFragmentScope
interface AnnotatedContactLocationFragmentComponent :
    ContactLocationFragmentComponent {
    @Subcomponent.Factory
    interface Factory : ContactLocationFragmentComponent.Factory {
        override fun create(): ContactLocationFragmentComponent
    }
}

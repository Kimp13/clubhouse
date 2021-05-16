package com.example.clubhouse.components

import com.example.clubhouse.modules.ViewContactLocationModule
import com.example.clubhouse.scopes.ViewContactLocationFragmentScope
import com.example.presentation.di.components.ViewContactLocationComponent
import dagger.Subcomponent

@Subcomponent(modules = [ViewContactLocationModule::class])
@ViewContactLocationFragmentScope
interface AnnotatedViewContactLocationFragmentComponent :
    ViewContactLocationComponent {
    @Subcomponent.Factory
    interface Factory : ViewContactLocationComponent.Factory {
        override fun create(): AnnotatedViewContactLocationFragmentComponent
    }
}
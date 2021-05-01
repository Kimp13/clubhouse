package com.example.clubhouse.components

import com.example.clubhouse.modules.ServiceModule
import com.example.clubhouse.scopes.ServiceScope
import com.example.presentation.services.ServiceComponent
import dagger.Subcomponent

@Subcomponent(modules = [ServiceModule::class])
@ServiceScope
interface AnnotatedServiceComponent : ServiceComponent {
    @Subcomponent.Factory
    interface Factory : ServiceComponent.Factory {
        override fun create(): AnnotatedServiceComponent
    }
}
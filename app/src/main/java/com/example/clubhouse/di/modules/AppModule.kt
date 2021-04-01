package com.example.clubhouse.di.modules

import android.app.Application
import com.example.clubhouse.di.components.ContactViewModelComponent
import com.example.clubhouse.ui.services.StartedContactServiceComponent
import com.example.clubhouse.ui.viewmodels.LocationViewModelComponent
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(
    subcomponents = [
        LocationViewModelComponent::class,
        ContactViewModelComponent::class,
        StartedContactServiceComponent::class
    ]
)
class AppModule(
    private val application: Application
) {
    @Provides
    @Singleton
    fun provideApplication() = application
}
package com.example.clubhouse.di.modules

import android.app.Application
import android.content.Context
import com.example.clubhouse.ui.fragments.ContactFragmentComponent
import com.example.clubhouse.ui.services.StartedContactServiceComponent
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(
    subcomponents = [
        StartedContactServiceComponent::class,
        ContactFragmentComponent::class
    ]
)
class AppModule(
    private val application: Application
) {
    @Provides
    @Singleton
    fun provideContext(): Context {
        return application
    }
}
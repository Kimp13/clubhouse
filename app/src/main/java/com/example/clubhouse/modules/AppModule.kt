package com.example.clubhouse.modules

import android.app.Application
import android.content.Context
import com.example.clubhouse.components.AnnotatedContactDetailsFragmentComponent
import com.example.clubhouse.components.AnnotatedContactListFragmentComponent
import com.example.clubhouse.components.AnnotatedServiceComponent
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(
    subcomponents = [
        AnnotatedServiceComponent::class,
        AnnotatedContactDetailsFragmentComponent::class,
        AnnotatedContactListFragmentComponent::class
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
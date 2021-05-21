package com.example.clubhouse.modules

import android.app.Application
import android.content.Context
import com.example.clubhouse.components.AnnotatedContactDetailsFragmentComponent
import com.example.clubhouse.components.AnnotatedContactListFragmentComponent
import com.example.clubhouse.components.AnnotatedServiceComponent
import com.example.clubhouse.components.AnnotatedViewContactLocationFragmentComponent
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(
    subcomponents = [
        AnnotatedServiceComponent::class,
        AnnotatedContactDetailsFragmentComponent::class,
        AnnotatedContactListFragmentComponent::class,
        AnnotatedViewContactLocationFragmentComponent::class
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

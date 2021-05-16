package com.example.clubhouse

import android.app.Application
import com.example.clubhouse.components.AnnotatedContactDetailsFragmentComponent
import com.example.clubhouse.components.AnnotatedContactListFragmentComponent
import com.example.clubhouse.components.AnnotatedContactLocationFragmentComponent
import com.example.clubhouse.components.AnnotatedServiceComponent
import com.example.clubhouse.components.AnnotatedViewContactLocationFragmentComponent
import com.example.clubhouse.modules.AppModule
import com.example.clubhouse.modules.DatabaseModule
import com.example.clubhouse.modules.RepositoryModule
import com.example.presentation.di.components.ApplicationComponent
import com.example.presentation.di.interfaces.AppComponentOwner
import dagger.Component
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Singleton

@Component(
    modules = [
        AppModule::class,
        RepositoryModule::class,
        DatabaseModule::class
    ]
)
@Singleton
interface AnnotatedApplicationComponent : ApplicationComponent {
    override fun serviceComponent(): AnnotatedServiceComponent.Factory

    override fun contactDetailsFragmentComponent():
            AnnotatedContactDetailsFragmentComponent.Factory

    override fun contactListFragmentComponent():
            AnnotatedContactListFragmentComponent.Factory

    override fun contactLocationFragmentComponent():
            AnnotatedContactLocationFragmentComponent.Factory

    override fun viewContactLocationFragmentComponent():
            AnnotatedViewContactLocationFragmentComponent.Factory
}

class ContactApplication : Application(), AppComponentOwner {
    override lateinit var applicationComponent: AnnotatedApplicationComponent

    override fun onCreate() {
        super.onCreate()

        applicationComponent = DaggerAnnotatedApplicationComponent.builder()
            .appModule(AppModule(this))
            .build()

        Timber.plant(DebugTree())
    }
}
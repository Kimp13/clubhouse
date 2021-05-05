package com.example.clubhouse

import android.app.Application
import com.example.clubhouse.components.AnnotatedContactDetailsFragmentComponent
import com.example.clubhouse.components.AnnotatedContactListFragmentComponent
import com.example.clubhouse.components.AnnotatedServiceComponent
import com.example.clubhouse.modules.AppModule
import com.example.clubhouse.modules.ContactRepositoryModule
import com.example.presentation.interfaces.AppComponentOwner
import com.example.presentation.interfaces.ApplicationComponent
import dagger.Component
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Singleton

@Component(
    modules = [AppModule::class, ContactRepositoryModule::class]
)
@Singleton
interface AnnotatedApplicationComponent : ApplicationComponent {
    override fun serviceComponent(): AnnotatedServiceComponent.Factory

    override fun contactDetailsFragmentComponent():
            AnnotatedContactDetailsFragmentComponent.Factory

    override fun contactListFragmentComponent():
            AnnotatedContactListFragmentComponent.Factory
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
package com.example.clubhouse

import android.app.Application
import com.example.clubhouse.di.components.ContactViewModelComponent
import com.example.clubhouse.di.modules.AppModule
import com.example.clubhouse.di.modules.DatabaseModule
import com.example.clubhouse.ui.services.StartedContactServiceComponent
import com.example.clubhouse.ui.viewmodels.LocationViewModelComponent
import dagger.Component
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Singleton

@Component(modules = [AppModule::class, DatabaseModule::class])
@Singleton
interface ApplicationComponent {
    fun locationViewModelComponent(): LocationViewModelComponent.Factory
    fun contactViewModelComponent(): ContactViewModelComponent.Factory
    fun startedContactServiceComponent(): StartedContactServiceComponent.Factory
}

class ContactApplication : Application() {
    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()

        applicationComponent = DaggerApplicationComponent.builder()
            .appModule(AppModule(this))
            .build()

        Timber.plant(DebugTree())
    }
}
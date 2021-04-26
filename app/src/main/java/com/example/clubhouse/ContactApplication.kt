package com.example.clubhouse

import android.app.Application
import com.example.clubhouse.di.modules.AppModule
import com.example.clubhouse.ui.fragments.ContactFragmentComponent
import com.example.clubhouse.ui.services.StartedContactServiceComponent
import dagger.Component
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface ApplicationComponent {
    fun startedContactServiceComponent(): StartedContactServiceComponent.Factory
    fun contactFragmentComponent(): ContactFragmentComponent.Factory
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
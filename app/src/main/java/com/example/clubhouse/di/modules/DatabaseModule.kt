package com.example.clubhouse.di.modules

import android.app.Application
import androidx.room.Room
import com.example.clubhouse.data.ContactDatabase
import com.example.clubhouse.data.daos.ContactLocationDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideContactLocationDao(db: ContactDatabase): ContactLocationDao {
        return db.contactLocationDao()
    }

    @Provides
    @Singleton
    fun provideDatabase(application: Application): ContactDatabase {
        return Room.databaseBuilder(
            application,
            ContactDatabase::class.java,
            "example_clubhouse"
        ).build()
    }
}
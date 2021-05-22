package com.example.clubhouse.modules

import android.content.Context
import androidx.room.Room
import com.example.clubhouse.BuildConfig
import com.example.presentation.data.AppDatabase
import com.example.presentation.data.daos.ContactLocationDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            BuildConfig.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideContactLocationDao(
        db: AppDatabase
    ): ContactLocationDao {
        return db.contactLocationDao()
    }
}

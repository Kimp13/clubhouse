package com.example.clubhouse.di.modules

import com.example.clubhouse.di.scopes.ViewModelScope
import com.google.gson.Gson
import dagger.Module
import dagger.Provides

@Module
class GsonModule {
    @Provides
    @ViewModelScope
    fun provideGson(): Gson {
        return Gson()
    }
}
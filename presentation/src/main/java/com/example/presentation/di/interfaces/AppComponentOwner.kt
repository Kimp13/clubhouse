package com.example.presentation.di.interfaces

import com.example.presentation.di.components.ApplicationComponent

interface AppComponentOwner {
    val applicationComponent: ApplicationComponent
}
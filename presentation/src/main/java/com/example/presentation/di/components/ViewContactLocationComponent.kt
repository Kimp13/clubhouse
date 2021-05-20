package com.example.presentation.di.components

import com.example.presentation.ui.fragments.ViewContactLocationFragment

interface ViewContactLocationComponent {
    interface Factory {
        fun create(): ViewContactLocationComponent
    }

    fun inject(fragment: ViewContactLocationFragment)
}
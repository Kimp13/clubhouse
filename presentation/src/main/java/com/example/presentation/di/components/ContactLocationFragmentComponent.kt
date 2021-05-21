package com.example.presentation.di.components

import com.example.presentation.ui.fragments.ContactLocationFragment

interface ContactLocationFragmentComponent {
    interface Factory {
        fun create(): ContactLocationFragmentComponent
    }

    fun inject(fragment: ContactLocationFragment)
}

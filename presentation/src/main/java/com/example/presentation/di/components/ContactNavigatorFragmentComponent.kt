package com.example.presentation.di.components

import com.example.presentation.ui.fragments.ContactNavigatorFragment

interface ContactNavigatorFragmentComponent {
    interface Factory {
        fun create(): ContactNavigatorFragmentComponent
    }

    fun inject(fragment: ContactNavigatorFragment)
}
package com.example.presentation.di.components

import com.example.presentation.ui.fragments.ContactDetailsFragment

interface ContactDetailsFragmentComponent {
    interface Factory {
        fun create(): ContactDetailsFragmentComponent
    }

    fun inject(fragment: ContactDetailsFragment)
}
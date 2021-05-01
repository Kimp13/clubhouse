package com.example.presentation.components

import com.example.presentation.fragments.ContactDetailsFragment

interface ContactDetailsFragmentComponent {
    interface Factory {
        fun create(): ContactDetailsFragmentComponent
    }

    fun inject(fragment: ContactDetailsFragment)
}
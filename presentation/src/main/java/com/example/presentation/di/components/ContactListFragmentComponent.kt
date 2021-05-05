package com.example.presentation.di.components

import com.example.presentation.ui.fragments.ContactListFragment

interface ContactListFragmentComponent {
    interface Factory {
        fun create(): ContactListFragmentComponent
    }

    fun inject(fragment: ContactListFragment)
}
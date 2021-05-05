package com.example.presentation.components

import com.example.presentation.fragments.ContactListFragment

interface ContactListFragmentComponent {
    interface Factory {
        fun create(): ContactListFragmentComponent
    }

    fun inject(fragment: ContactListFragment)
}
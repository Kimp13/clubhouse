package com.example.presentation.di.components

import com.example.presentation.ui.fragments.base.BaseContactListFragment

interface ContactListFragmentComponent {
    interface Factory {
        fun create(): ContactListFragmentComponent
    }

    fun inject(fragment: BaseContactListFragment)
}
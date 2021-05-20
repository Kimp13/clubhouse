package com.example.presentation.di.components

import com.example.presentation.ui.services.ServiceComponent

interface ApplicationComponent {
    fun contactListFragmentComponent(): ContactListFragmentComponent.Factory
    fun contactLocationFragmentComponent():
            ContactLocationFragmentComponent.Factory

    fun contactDetailsFragmentComponent():
            ContactDetailsFragmentComponent.Factory

    fun viewContactLocationFragmentComponent():
            ViewContactLocationComponent.Factory

    fun contactNavigatorFragmentComponent():
            ContactNavigatorFragmentComponent.Factory

    fun serviceComponent(): ServiceComponent.Factory
}
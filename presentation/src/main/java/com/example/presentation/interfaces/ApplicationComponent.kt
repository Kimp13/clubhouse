package com.example.presentation.interfaces

import com.example.presentation.components.ContactDetailsFragmentComponent
import com.example.presentation.components.ContactListFragmentComponent
import com.example.presentation.services.ServiceComponent

interface ApplicationComponent {
    fun contactListFragmentComponent(): ContactListFragmentComponent.Factory
    fun contactDetailsFragmentComponent():
            ContactDetailsFragmentComponent.Factory

    fun serviceComponent(): ServiceComponent.Factory
}
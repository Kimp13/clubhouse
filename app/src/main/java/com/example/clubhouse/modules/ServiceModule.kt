package com.example.clubhouse.modules

import com.example.clubhouse.scopes.ServiceScope
import com.example.domain.interactors.implementations.BirthdayNotificationServiceInteractor
import com.example.domain.interactors.implementations.RebootReminderServiceInteractor
import com.example.domain.interactors.interfaces.ContactDetailsInteractor
import com.example.domain.interactors.interfaces.ContactListInteractor
import com.example.domain.repositories.ContactRepository
import dagger.Module
import dagger.Provides

@Module
class ServiceModule {
    @Provides
    @ServiceScope
    fun provideContactDetailsInteractor(
        contactRepository: ContactRepository
    ): ContactDetailsInteractor {
        return BirthdayNotificationServiceInteractor(contactRepository)
    }

    @Provides
    @ServiceScope
    fun provideContactListInteractor(
        contactRepository: ContactRepository
    ): ContactListInteractor {
        return RebootReminderServiceInteractor(contactRepository)
    }
}
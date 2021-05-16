package com.example.clubhouse.modules

import com.example.clubhouse.scopes.ServiceScope
import com.example.domain.interactors.implementations.BirthdayNotificationInteractor
import com.example.domain.interactors.implementations.RebootReminderInteractor
import com.example.domain.interactors.interfaces.ContactDetailsInteractor
import com.example.domain.interactors.interfaces.LookupContactListInteractor
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
        return BirthdayNotificationInteractor(contactRepository)
    }

    @Provides
    @ServiceScope
    fun provideContactListInteractor(
        contactRepository: ContactRepository
    ): LookupContactListInteractor {
        return RebootReminderInteractor(contactRepository)
    }
}
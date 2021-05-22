package com.example.clubhouse.modules

import com.example.clubhouse.qualifiers.ContactSharedPreferences
import com.example.clubhouse.scopes.ServiceScope
import com.example.domain.interactors.implementations.BirthdayNotificationInteractor
import com.example.domain.interactors.implementations.RebootReminderInteractor
import com.example.domain.repositories.interfaces.BasicTypesRepository
import com.example.domain.repositories.interfaces.ContactRepository
import com.example.domain.repositories.interfaces.DateTimeRepository
import com.example.domain.repositories.interfaces.ReminderRepository
import dagger.Module
import dagger.Provides

@Module
class ServiceModule {
    @Provides
    @ServiceScope
    fun provideBirthdayNotificationInteractor(
        contactRepository: ContactRepository,
        reminderRepository: ReminderRepository,
        dateTimeRepository: DateTimeRepository
    ): BirthdayNotificationInteractor {
        return BirthdayNotificationInteractor(
            contactRepository,
            reminderRepository,
            dateTimeRepository
        )
    }

    @Provides
    @ServiceScope
    fun provideRebootReminderInteractor(
        contactRepository: ContactRepository,
        reminderRepository: ReminderRepository,
        dateTimeRepository: DateTimeRepository,

        @ContactSharedPreferences
        basicTypesRepository: BasicTypesRepository
    ): RebootReminderInteractor {
        return RebootReminderInteractor(
            contactRepository,
            reminderRepository,
            dateTimeRepository,
            basicTypesRepository
        )
    }
}

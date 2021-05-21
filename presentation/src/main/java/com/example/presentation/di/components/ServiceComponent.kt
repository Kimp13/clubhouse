package com.example.presentation.di.components

import com.example.presentation.ui.services.BirthdayNotificationService
import com.example.presentation.ui.services.RebootReminderService

interface ServiceComponent {
    interface Factory {
        fun create(): ServiceComponent
    }

    fun inject(service: BirthdayNotificationService)
    fun inject(service: RebootReminderService)
}

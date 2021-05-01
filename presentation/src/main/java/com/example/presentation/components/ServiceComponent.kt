package com.example.presentation.components

import com.example.presentation.services.BirthdayNotificationService
import com.example.presentation.services.RebootReminderService

interface ServiceComponent {
    interface Factory {
        fun create(): ServiceComponent
    }

    fun inject(service: BirthdayNotificationService)
    fun inject(service: RebootReminderService)
}
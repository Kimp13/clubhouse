package com.example.presentation.data.repositories

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.domain.entities.ContactEntity
import com.example.domain.repositories.interfaces.ReminderRepository
import com.example.presentation.R
import com.example.presentation.ui.fragments.CONTACT_ARG_LOOKUP_KEY
import com.example.presentation.ui.receivers.ContactBirthdayReceiver
import java.lang.ref.WeakReference
import java.util.Calendar

class AlarmRepository(
    context: Context
) : ReminderRepository {
    private val contextReference = WeakReference(context)
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE)
        as AlarmManager

    override fun setReminder(contactEntity: ContactEntity, toTime: Calendar) {
        contextReference.get()?.let { context ->
            contactEntity.birthDate?.let {
                alarmManager.set(
                    AlarmManager.RTC,
                    toTime.timeInMillis,
                    createPendingIntent(context, contactEntity)
                )
            }
        }
    }

    override fun clearReminder(contactEntity: ContactEntity) {
        contextReference.get()?.let { context ->
            getPendingIntent(context, contactEntity)?.let {
                alarmManager.cancel(it)
                it.cancel()
            }
        }
    }

    override fun hasReminder(contactEntity: ContactEntity): Boolean {
        return contextReference.get()?.let {
            getPendingIntent(it, contactEntity)
        } != null
    }

    private fun getPendingIntent(
        context: Context,
        contact: ContactEntity
    ): PendingIntent? {
        return PendingIntent.getBroadcast(
            context,
            contact.id.toInt(),
            createIntent(context, contact),
            PendingIntent.FLAG_NO_CREATE
        )
    }

    private fun createIntent(context: Context, contact: ContactEntity): Intent {
        return Intent(
            context,
            ContactBirthdayReceiver::class.java
        ).apply {
            action = context.getString(R.string.birthday_action)
            putExtra(CONTACT_ARG_LOOKUP_KEY, contact.lookup)
        }
    }

    private fun createPendingIntent(
        context: Context,
        contact: ContactEntity
    ) = PendingIntent.getBroadcast(
        context,
        contact.id.toInt(),
        createIntent(context, contact),
        0
    )
}

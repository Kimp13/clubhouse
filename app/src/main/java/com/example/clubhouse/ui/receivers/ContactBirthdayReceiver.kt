package com.example.clubhouse.ui.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.clubhouse.R
import com.example.clubhouse.ui.delegates.ReminderDelegate
import com.example.clubhouse.ui.fragments.CONTACT_ARG_LOOKUP_KEY
import com.example.clubhouse.ui.services.BirthdayNotificationService

class ContactBirthdayReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            context.getString(R.string.birthday_action) -> {
                intent.getStringExtra(CONTACT_ARG_LOOKUP_KEY).let { lookup ->
                    Intent(
                        context, BirthdayNotificationService::class.java
                    ).apply {
                        putExtra(CONTACT_ARG_LOOKUP_KEY, lookup)
                    }.let {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(it)
                        } else {
                            context.startService(it)
                        }
                    }
                }
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                ReminderDelegate.resetAllContacts(context)
            }
        }
    }
}
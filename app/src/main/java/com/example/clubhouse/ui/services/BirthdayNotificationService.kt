package com.example.clubhouse.ui.services

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.clubhouse.R
import com.example.clubhouse.data.entities.ContactEntity
import com.example.clubhouse.ui.activities.MainActivity
import com.example.clubhouse.ui.delegates.ReminderDelegate
import com.example.clubhouse.ui.fragments.CONTACT_ARG_LOOKUP_KEY
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import timber.log.Timber

private const val FOREGROUND_NOTIFICATION_ID = -0b1011010

class BirthdayNotificationService : StartedContactService() {
    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        super.onStartCommand(intent, flags, startId)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initializeForeground()
        }

        intent?.getStringExtra(CONTACT_ARG_LOOKUP_KEY)?.let { lookup ->
            if (checkReadContactsPermission()) {
                launch {
                    try {
                        repository.getContact(
                            this@BirthdayNotificationService,
                            lookup
                        )?.let { contact ->
                            showBirthdayNotification(contact)
                            ReminderDelegate.setReminder(
                                this@BirthdayNotificationService,
                                contact
                            )
                        }
                    } catch (e: CancellationException) {
                        Timber.d("Service job cancelled\n$e")
                    } finally {
                        stopForeground(true)
                        stopSelf()
                    }
                }

                return START_NOT_STICKY
            }
        }

        stopForeground(true)
        stopSelf()
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun initializeForeground() {
        startForeground(
            FOREGROUND_NOTIFICATION_ID,
            NotificationCompat.Builder(
                this,
                getString(R.string.birthday_channel_id)
            )
                .setContentTitle(
                    getString(
                        R.string.happy_birthday
                    )
                )
                .setContentText(
                    getString(
                        R.string.getting_data_about_birthday_kid
                    )
                )
                .setSmallIcon(
                    R.drawable.ic_baseline_person_24
                )
                .build()
        )
    }

    private fun showBirthdayNotification(contact: ContactEntity) {
        NotificationManagerCompat.from(this).notify(
            contact.id.toInt(),
            NotificationCompat
                .Builder(
                    this,
                    getString(
                        R.string.birthday_channel_id
                    )
                )
                .setSmallIcon(R.drawable.ic_baseline_person_24)
                .setContentTitle(
                    getString(
                        R.string.someone_has_birthday_today_fmt,
                        contact.name
                    )
                )
                .setContentText(getString(R.string.birthday_content_text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(
                    PendingIntent.getActivity(
                        this,
                        contact.id.toInt(),
                        Intent(this, MainActivity::class.java).apply {
                            putExtra(CONTACT_ARG_LOOKUP_KEY, contact.lookup)

                            action = getString(
                                R.string.birthday_notification_action
                            )
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                        },
                        0
                    )
                )
                .setAutoCancel(true)
                .build()
        )
    }
}
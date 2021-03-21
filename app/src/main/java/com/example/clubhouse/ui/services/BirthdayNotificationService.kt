package com.example.clubhouse.ui.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.clubhouse.R
import com.example.clubhouse.data.ContactEntity
import com.example.clubhouse.data.MockDataSource
import com.example.clubhouse.ui.activities.CONTACT_ARG_NULL_VALUE
import com.example.clubhouse.ui.activities.MainActivity
import com.example.clubhouse.ui.delegates.ReminderDelegate
import com.example.clubhouse.ui.fragments.CONTACT_ARG_ID
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

private const val FOREGROUND_NOTIFICATION_ID = -0b1011010

class BirthdayNotificationService : Service(), CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initializeForeground()
        }

        intent?.getIntExtra(
            CONTACT_ARG_ID,
            CONTACT_ARG_NULL_VALUE
        )?.let { id ->
            if (id != CONTACT_ARG_NULL_VALUE) {
                launch {
                    try {
                        delay(2000)

                        val contact = MockDataSource.getContact(id)

                        showBirthdayNotification(contact)
                        ReminderDelegate.setReminder(
                            this@BirthdayNotificationService,
                            contact
                        )
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

    override fun onDestroy() {
        job.cancel()

        super.onDestroy()
    }

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
            contact.id,
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
                        contact.id,
                        Intent(this, MainActivity::class.java).apply {
                            putExtra(CONTACT_ARG_ID, contact.id)

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
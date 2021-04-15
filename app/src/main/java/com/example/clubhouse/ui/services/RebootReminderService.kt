package com.example.clubhouse.ui.services

import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.clubhouse.R
import com.example.clubhouse.data.ContactRepository
import com.example.clubhouse.ui.delegates.CONTACT_LOOKUPS_ARRAY_KEY
import com.example.clubhouse.ui.delegates.ReminderDelegate
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers

private const val FOREGROUND_NOTIFICATION_ID = -0b10010101

class RebootReminderService : StartedContactService() {
    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initializeForeground()
        }

        intent?.extras?.getStringArrayList(CONTACT_LOOKUPS_ARRAY_KEY)
            ?.let { lookups ->
                if (checkReadContactsPermission()) {
                    ContactRepository.getContacts(
                        this@RebootReminderService,
                        lookups
                    )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doFinally {
                            stopForeground(true)
                            stopSelf()
                        }
                        .subscribe { contacts ->
                            contacts.forEach {
                                ReminderDelegate.setReminder(
                                    this@RebootReminderService,
                                    it
                                )
                            }
                        }
                        .addTo(disposable)

                    return START_NOT_STICKY
                }
            }

        stopForeground(true)
        stopSelf()
        return START_NOT_STICKY
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
                        R.string.birthday_presents
                    )
                )
                .setContentText(
                    getString(
                        R.string.getting_data_about_birthday_kids
                    )
                )
                .setSmallIcon(
                    R.drawable.ic_baseline_person_24
                )
                .build()
        )
    }
}
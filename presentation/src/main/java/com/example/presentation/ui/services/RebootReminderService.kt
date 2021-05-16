package com.example.presentation.ui.services

import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.domain.interactors.implementations.RebootReminderInteractor
import com.example.presentation.R
import com.example.presentation.di.interfaces.AppComponentOwner
import com.example.presentation.ui.delegates.CONTACT_LOOKUPS_ARRAY_KEY
import com.example.presentation.ui.delegates.ReminderDelegate
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import timber.log.Timber

private const val FOREGROUND_NOTIFICATION_ID = -0b10010101

abstract class RebootReminderService : StartedContactService() {
    abstract val interactor: RebootReminderInteractor

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        (application as? AppComponentOwner)?.applicationComponent
            ?.serviceComponent()
            ?.create()
            ?.inject(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initializeForeground()
        }

        intent?.extras?.getStringArrayList(CONTACT_LOOKUPS_ARRAY_KEY)
            ?.let { lookups ->
                if (checkReadContactsPermission()) {
                    launch {
                        try {
                            interactor.getContacts(lookups).forEach {
                                ReminderDelegate.setReminder(
                                    this@RebootReminderService,
                                    it
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
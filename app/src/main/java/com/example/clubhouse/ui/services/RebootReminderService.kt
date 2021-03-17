package com.example.clubhouse.ui.services

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.clubhouse.R
import com.example.clubhouse.data.MockDataSource
import com.example.clubhouse.ui.delegates.CONTACT_IDS_ARRAY_KEY
import com.example.clubhouse.ui.delegates.ReminderDelegate
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

private const val FOREGROUND_NOTIFICATION_ID = -0b10010101

class RebootReminderService : Service(), CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initializeForeground()
        }

        intent?.getIntArrayExtra(CONTACT_IDS_ARRAY_KEY)?.let { ids ->
            launch {
                try {
                    delay(10000)

                    MockDataSource.getContacts(ids).forEach {
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
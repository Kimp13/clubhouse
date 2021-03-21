package com.example.clubhouse.ui.delegates

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import com.example.clubhouse.R
import com.example.clubhouse.data.ContactEntity
import com.example.clubhouse.data.isLeap
import com.example.clubhouse.ui.fragments.CONTACT_ARG_ID
import com.example.clubhouse.ui.receivers.ContactBirthdayReceiver
import com.example.clubhouse.ui.services.RebootReminderService
import java.util.*

const val CONTACT_SHARED_PREFERENCES_KEY = "contact_shared_prefs"
const val CONTACT_IDS_ARRAY_KEY = "contact_ids_array"

object ReminderDelegate {
    private var alarmManager: AlarmManager? = null
    private var alarmIntent: PendingIntent? = null
    private var intent: Intent? = null
    private var sharedPreferences: SharedPreferences? = null

    fun setReminder(context: Context, contact: ContactEntity) {
        checkAlarmManager(context)

        if (!isReminderSet(context, contact)) {
            alarmIntent = PendingIntent.getBroadcast(
                context,
                contact.id,
                intent,
                0
            )
        }

        writeContactId(contact.id)
        scheduleAlarm(contact)
    }

    fun clearReminder(context: Context, contactId: Int) {
        checkAlarmManager(context)
        checkSharedPreferences(context)

        alarmManager?.cancel(alarmIntent)
        alarmIntent?.cancel()
        alarmIntent = null

        sharedPreferences
            ?.edit()
            ?.remove(contactId.toString())
            ?.apply()
    }

    fun isReminderSet(context: Context, contact: ContactEntity): Boolean {
        checkIntent(context, contact)

        alarmIntent = PendingIntent.getBroadcast(
            context,
            contact.id,
            intent,
            PendingIntent.FLAG_NO_CREATE
        )

        return alarmIntent != null
    }

    fun resetAllContacts(context: Context) {
        checkSharedPreferences(context)

        sharedPreferences?.all?.run {
            Intent(
                context,
                RebootReminderService::class.java
            ).apply {
                putExtra(
                    CONTACT_IDS_ARRAY_KEY,
                    keys.filterNotNull().map {
                        it.toInt()
                    }.toIntArray()
                )
            }.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(it)
                } else {
                    context.startService(it)
                }
            }
        }
    }

    private fun writeContactId(contactId: Int) {
        sharedPreferences
            ?.edit()
            ?.putBoolean(contactId.toString(), true)
            ?.apply()
    }

    private fun checkSharedPreferences(context: Context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(
                CONTACT_SHARED_PREFERENCES_KEY,
                Context.MODE_PRIVATE
            )
        }
    }

    private fun scheduleAlarm(contact: ContactEntity) {
        Calendar.getInstance().run {
            val previous = timeInMillis

            set(Calendar.MONTH, contact.birthDate.month)
            set(Calendar.DAY_OF_MONTH, contact.birthDate.day)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (timeInMillis <= previous) {
                set(Calendar.YEAR, get(Calendar.YEAR) + 1)
            }

            if (contact.birthDate.isLeap()) {
                while (!isLeap()) {
                    set(Calendar.YEAR, (get(Calendar.YEAR) / 4 + 1) * 4)
                }
            }

            alarmManager?.set(
                AlarmManager.RTC,
                timeInMillis,
                alarmIntent
            )
        }
    }

    private fun createAlarmIntent(
        context: Context,
        contact: ContactEntity
    ) = Intent(
        context,
        ContactBirthdayReceiver::class.java
    ).apply {
        action = context.getString(R.string.birthday_action)
        putExtra(CONTACT_ARG_ID, contact.id)
    }

    private fun checkAlarmManager(context: Context) {
        if (alarmManager == null) {
            alarmManager = context.getSystemService(Context.ALARM_SERVICE)
                    as? AlarmManager
        }
    }

    private fun checkIntent(context: Context, contact: ContactEntity) {
        if (intent == null) {
            intent = createAlarmIntent(context, contact)
        }
    }
}
package com.example.presentation.delegates

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import com.example.domain.entities.ContactEntity
import com.example.domain.isLeap
import com.example.presentation.R
import com.example.presentation.fragments.CONTACT_ARG_LOOKUP_KEY
import com.example.presentation.receivers.ContactBirthdayReceiver
import com.example.presentation.services.RebootReminderService
import java.util.*

const val CONTACT_SHARED_PREFERENCES_KEY = "contact_shared_prefs"
const val CONTACT_LOOKUPS_ARRAY_KEY = "contact_lookups_array"

object ReminderDelegate {
    private var alarmManager: AlarmManager? = null
    private var alarmIntent: PendingIntent? = null
    private var intent: Intent? = null
    private var sharedPreferences: SharedPreferences? = null

    fun setReminder(
        context: Context,
        contact: ContactEntity
    ) {
        checkAlarmManager(context)

        if (!isReminderSet(context, contact)) {
            alarmIntent = PendingIntent.getBroadcast(
                context,
                contact.id.toInt(),
                intent,
                0
            )
        }

        writeContactInfo(contact.id, contact.lookup)
        scheduleAlarm(contact)
    }

    fun clearReminder(context: Context, contactId: Long) {
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

    fun isReminderSet(
        context: Context,
        contact: ContactEntity
    ): Boolean {
        checkIntent(context, contact)

        alarmIntent = PendingIntent.getBroadcast(
            context,
            contact.id.toInt(),
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
                val valuesList = arrayListOf<String?>()

                values.forEach {
                    valuesList.add(it as? String)
                }

                putStringArrayListExtra(
                    CONTACT_LOOKUPS_ARRAY_KEY,
                    valuesList
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

    private fun writeContactInfo(contactId: Long, contactKey: String) {
        sharedPreferences
            ?.edit()
            ?.putString(contactId.toString(), contactKey)
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
            contact.birthDate?.let {
                val previous = timeInMillis

                set(Calendar.MONTH, it.month)
                set(Calendar.DAY_OF_MONTH, it.day)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

                if (timeInMillis <= previous) {
                    set(Calendar.YEAR, get(Calendar.YEAR) + 1)
                }

                if (it.isLeap()) {
                    while (!isLeap()) {
                        set(Calendar.YEAR, (get(Calendar.YEAR) / 4 + 1) * 4)
                    }

                    set(Calendar.MONTH, it.month)
                    set(Calendar.DAY_OF_MONTH, it.day)
                }

                alarmManager?.set(
                    AlarmManager.RTC,
                    timeInMillis,
                    alarmIntent
                )
            }
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
        putExtra(CONTACT_ARG_LOOKUP_KEY, contact.lookup)
    }

    private fun checkAlarmManager(context: Context) {
        if (alarmManager == null) {
            alarmManager = context.getSystemService(Context.ALARM_SERVICE)
                    as? AlarmManager
        }
    }

    private fun checkIntent(
        context: Context,
        contact: ContactEntity
    ) {
        if (intent == null) {
            intent = createAlarmIntent(context, contact)
        }
    }
}
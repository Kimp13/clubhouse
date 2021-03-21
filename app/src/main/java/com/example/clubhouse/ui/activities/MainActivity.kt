package com.example.clubhouse.ui.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.clubhouse.R
import com.example.clubhouse.ui.fragments.*
import com.example.clubhouse.ui.interfaces.ContactCardClickListener
import com.example.clubhouse.ui.interfaces.ContactServiceConsumer
import com.example.clubhouse.ui.interfaces.ContactServiceOwner
import com.example.clubhouse.ui.services.ContactService
import kotlin.properties.Delegates

const val CONTACT_ARG_NULL_VALUE = -1

private const val FRAGMENT_TAG_KEY = "fragment_tag"

class MainActivity : AppCompatActivity(), ContactCardClickListener, ContactServiceOwner {
    private var bound = false
    private lateinit var contactService: ContactService
    private var currentFragmentTag: String by Delegates.notNull()

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, iBinder: IBinder) {
            bound = true
            contactService = (iBinder as ContactService.ContactBinder).getService()

            supportFragmentManager.findFragmentByTag(currentFragmentTag)?.let {
                if (it is ContactServiceConsumer) {
                    it.onServiceBoundListener()
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()

        bindService(
            Intent(this, ContactService::class.java),
            connection,
            Context.BIND_AUTO_CREATE
        )

        currentFragmentTag = savedInstanceState?.getString(FRAGMENT_TAG_KEY)
            ?: run {
                supportFragmentManager
                    .beginTransaction()
                    .add(
                        R.id.mainActivityRootLayout,
                        ContactListFragment(),
                        CONTACT_LIST_FRAGMENT_TAG
                    )
                    .commit()

                CONTACT_LIST_FRAGMENT_TAG
            }

        intent?.getIntExtra(
            CONTACT_ARG_ID,
            CONTACT_ARG_NULL_VALUE
        )?.let {
            if (it != CONTACT_ARG_NULL_VALUE) {
                onCardClick(it)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(FRAGMENT_TAG_KEY, currentFragmentTag)

        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        if (bound) {
            unbindService(connection)

            bound = false
        }

        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCardClick(id: Int) {
        currentFragmentTag = CONTACT_DETAILS_FRAGMENT_TAG

        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.mainActivityRootLayout,
                ContactDetailsFragment.newInstance(id),
                CONTACT_DETAILS_FRAGMENT_TAG
            )
            .addToBackStack(null)
            .commit()
    }

    override fun getService(): ContactService? =
        if (bound) {
            contactService
        } else {
            null
        }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(NotificationChannel(
                    getString(R.string.birthday_channel_id),
                    getString(R.string.birthday_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = getString(R.string.birthday_channel_description)
                })
        }
    }
}
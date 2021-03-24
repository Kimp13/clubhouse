package com.example.clubhouse.ui.activities

import android.Manifest
import android.app.ActionBar
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.clubhouse.R
import com.example.clubhouse.ui.fragments.CONTACT_ARG_LOOKUP_KEY
import com.example.clubhouse.ui.fragments.CONTACT_DETAILS_FRAGMENT_TAG
import com.example.clubhouse.ui.fragments.CONTACT_LIST_FRAGMENT_TAG
import com.example.clubhouse.ui.fragments.ContactDetailsFragment
import com.example.clubhouse.ui.fragments.ContactListFragment
import com.example.clubhouse.ui.fragments.REQUEST_READ_CONTACTS_PERMISSION_FRAGMENT_TAG
import com.example.clubhouse.ui.fragments.RequestPermissionDialogFragment
import com.example.clubhouse.ui.fragments.RequestReadContactsPermissionFragment
import com.example.clubhouse.ui.interfaces.ContactCardClickListener
import com.example.clubhouse.ui.interfaces.ContactServiceConsumer
import com.example.clubhouse.ui.interfaces.ContactServiceOwner
import com.example.clubhouse.ui.interfaces.PoppableBackStackOwner
import com.example.clubhouse.ui.interfaces.ReadContactsPermissionRequester
import com.example.clubhouse.ui.interfaces.RequestPermissionDialogDismissListener
import com.example.clubhouse.ui.services.ContactService
import kotlin.properties.Delegates

private const val FRAGMENT_TAG_KEY = "fragment_tag"

class MainActivity :
    AppCompatActivity(),
    ContactCardClickListener,
    ContactServiceOwner,
    PoppableBackStackOwner,
    ReadContactsPermissionRequester,
    RequestPermissionDialogDismissListener {
    private var bound = false
    private lateinit var contactService: ContactService
    private var currentFragmentTag: String by Delegates.notNull()
    private var requestSuccessCallback: (() -> Unit)? = null
    private var requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            requestSuccessCallback?.invoke()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_CONTACTS
                )
            ) {
                RequestPermissionDialogFragment().show(
                    supportFragmentManager,
                    null
                )
            } else {
                currentFragmentTag = REQUEST_READ_CONTACTS_PERMISSION_FRAGMENT_TAG

                supportFragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.mainActivityRootLayout,
                        RequestReadContactsPermissionFragment(),
                        currentFragmentTag
                    )
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

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

        intent?.getStringExtra(
            CONTACT_ARG_LOOKUP_KEY
        )?.let {
            onCardClick(it)
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

    override fun onBackPressed() {
        supportActionBar?.displayOptions?.run {
            if (and(ActionBar.DISPLAY_HOME_AS_UP) != 0) {
                supportFragmentManager.popBackStack()
                return
            }
        }

        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCardClick(lookup: String) {
        currentFragmentTag = CONTACT_DETAILS_FRAGMENT_TAG

        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.mainActivityRootLayout,
                ContactDetailsFragment.newInstance(lookup),
                CONTACT_DETAILS_FRAGMENT_TAG
            )
            .addToBackStack(null)
            .commit()
    }

    override fun popBackStack() {
        supportFragmentManager.popBackStack()
    }

    override fun getService(): ContactService? =
        if (bound) {
            contactService
        } else {
            null
        }

    override fun checkPermission() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.READ_CONTACTS
    ) == PackageManager.PERMISSION_GRANTED

    override fun requestPermission(onSuccess: () -> Unit) {
        if (checkPermission()) {
            onSuccess()
        } else {
            requestSuccessCallback = onSuccess
            requestPermissionLauncher.launch(
                Manifest.permission.READ_CONTACTS
            )
        }
    }

    override fun onRequestDialogDismissed() {
        requestPermissionLauncher.launch(
            Manifest.permission.READ_CONTACTS
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(
                    NotificationChannel(
                        getString(R.string.birthday_channel_id),
                        getString(R.string.birthday_channel_name),
                        NotificationManager.IMPORTANCE_DEFAULT
                    ).apply {
                        description = getString(R.string.birthday_channel_description)
                    })
        }
    }


}
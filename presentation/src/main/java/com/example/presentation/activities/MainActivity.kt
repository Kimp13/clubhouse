package com.example.presentation.activities

import android.Manifest
import android.app.ActionBar
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.presentation.R
import com.example.presentation.fragments.CONTACT_ARG_LOOKUP_KEY
import com.example.presentation.fragments.CONTACT_DETAILS_FRAGMENT_TAG
import com.example.presentation.fragments.CONTACT_LIST_FRAGMENT_TAG
import com.example.presentation.fragments.ContactDetailsFragment
import com.example.presentation.fragments.ContactListFragment
import com.example.presentation.fragments.REQUEST_READ_CONTACTS_PERMISSION_FRAGMENT_TAG
import com.example.presentation.fragments.RequestPermissionDialogFragment
import com.example.presentation.fragments.RequestReadContactsPermissionFragment
import com.example.presentation.interfaces.ContactCardClickListener
import com.example.presentation.interfaces.PoppableBackStackOwner
import com.example.presentation.interfaces.ReadContactsPermissionRequester
import com.example.presentation.interfaces.RequestPermissionDialogDismissListener
import kotlin.properties.Delegates

private const val FRAGMENT_TAG_KEY = "fragment_tag"

class MainActivity :
    AppCompatActivity(),
    ContactCardClickListener,
    PoppableBackStackOwner,
    ReadContactsPermissionRequester,
    RequestPermissionDialogDismissListener {
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
                changeFragment(
                    RequestReadContactsPermissionFragment(),
                    REQUEST_READ_CONTACTS_PERMISSION_FRAGMENT_TAG,
                    true
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()

        savedInstanceState?.getString(FRAGMENT_TAG_KEY)?.let {
            currentFragmentTag = it
        } ?: changeFragment(
            ContactListFragment(),
            CONTACT_LIST_FRAGMENT_TAG
        )

        intent?.getStringExtra(CONTACT_ARG_LOOKUP_KEY)?.let {
            onCardClick(it)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(FRAGMENT_TAG_KEY, currentFragmentTag)

        super.onSaveInstanceState(outState)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    override fun onCardClick(lookup: String) {
        changeFragment(
            ContactDetailsFragment.newInstance(lookup),
            CONTACT_DETAILS_FRAGMENT_TAG,
            true
        )
    }

    override fun popBackStack() {
        supportFragmentManager.popBackStack()
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
                        description =
                            getString(R.string.birthday_channel_description)
                    })
        }
    }

    private fun changeFragment(
        fragment: Fragment,
        tag: String,
        addToBackStack: Boolean = false
    ) {
        currentFragmentTag = tag

        supportFragmentManager.beginTransaction()
            .apply {
                replace(
                    R.id.mainActivityFragmentLayout,
                    fragment,
                    tag
                )

                if (addToBackStack) {
                    addToBackStack(null)
                }

                commit()
            }
    }
}
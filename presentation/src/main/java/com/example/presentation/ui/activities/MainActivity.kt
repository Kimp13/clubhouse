package com.example.presentation.ui.activities

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
import com.example.domain.entities.ContactEntity
import com.example.presentation.R
import com.example.presentation.ui.fragments.CONTACT_ARG_LOOKUP_KEY
import com.example.presentation.ui.fragments.CONTACT_DETAILS_FRAGMENT_TAG
import com.example.presentation.ui.fragments.CONTACT_LIST_FRAGMENT_TAG
import com.example.presentation.ui.fragments.CONTACT_LOCATION_FRAGMENT_TAG
import com.example.presentation.ui.fragments.ContactDetailsFragment
import com.example.presentation.ui.fragments.ContactListFragment
import com.example.presentation.ui.fragments.ContactLocationFragment
import com.example.presentation.ui.fragments.REQUEST_READ_CONTACTS_PERMISSION_FRAGMENT_TAG
import com.example.presentation.ui.fragments.RequestPermissionDialogFragment
import com.example.presentation.ui.fragments.RequestReadContactsPermissionFragment
import com.example.presentation.ui.fragments.VIEW_CONTACT_LOCATION_FRAGMENT_TAG
import com.example.presentation.ui.fragments.ViewContactLocationFragment
import com.example.presentation.ui.interfaces.AccessLocationPermissionRequester
import com.example.presentation.ui.interfaces.ContactCardClickListener
import com.example.presentation.ui.interfaces.ContactLocationRetriever
import com.example.presentation.ui.interfaces.ContactLocationViewer
import com.example.presentation.ui.interfaces.PoppableBackStackOwner
import com.example.presentation.ui.interfaces.ReadContactsPermissionRequester
import com.example.presentation.ui.interfaces.RequestPermissionDialogDismissListener
import kotlin.properties.Delegates

private const val FRAGMENT_TAG_KEY = "fragment_tag"

class MainActivity :
    AppCompatActivity(),
    ContactCardClickListener,
    ContactLocationRetriever,
    ContactLocationViewer,
    PoppableBackStackOwner,
    AccessLocationPermissionRequester,
    ReadContactsPermissionRequester,
    RequestPermissionDialogDismissListener {
    private var currentFragmentTag: String by Delegates.notNull()
    private var onRequestLocation: ((Boolean) -> Unit)? = null
    private var onRequestContactSuccess: (() -> Unit)? = null
    private val requestAccessLocation = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        onRequestLocation?.invoke(it)
    }
    private val requestReadContacts = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onRequestContactSuccess?.invoke()
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

    override fun retrieveContactLocation(contact: ContactEntity) {
        changeFragment(
            ContactLocationFragment.newInstance(contact),
            CONTACT_LOCATION_FRAGMENT_TAG,
            true
        )
    }

    override fun viewContactLocation(contactEntity: ContactEntity) {
        changeFragment(
            ViewContactLocationFragment.newInstance(contactEntity.id),
            VIEW_CONTACT_LOCATION_FRAGMENT_TAG,
            true
        )
    }

    override fun viewAllContactsLocation() {
        changeFragment(
            ViewContactLocationFragment(),
            VIEW_CONTACT_LOCATION_FRAGMENT_TAG,
            true
        )
    }

    override fun popBackStack() {
        supportFragmentManager.popBackStack()
    }

    override fun checkLocationPermission() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    override fun checkContactPermission() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.READ_CONTACTS
    ) == PackageManager.PERMISSION_GRANTED

    override fun requestLocationPermission(handler: (Boolean) -> Unit) {
        if (checkLocationPermission()) {
            handler(true)
        } else {
            onRequestLocation = handler
            requestAccessLocation.launch(
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    }

    override fun requestContactPermission(onSuccess: () -> Unit) {
        if (checkContactPermission()) {
            onSuccess()
        } else {
            onRequestContactSuccess = onSuccess
            requestReadContacts.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    override fun onRequestDialogDismissed() {
        requestReadContacts.launch(
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
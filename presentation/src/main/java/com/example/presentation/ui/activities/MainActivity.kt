package com.example.presentation.ui.activities

import android.app.ActionBar
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.presentation.R
import com.example.presentation.ui.activities.gateways.DialogFragmentGatewayImpl
import com.example.presentation.ui.activities.gateways.FragmentStackGatewayImpl
import com.example.presentation.ui.activities.gateways.PermissionGatewayImpl
import com.example.presentation.ui.activities.helpers.FragmentTransactionHelper
import com.example.presentation.ui.fragments.CONTACT_ARG_LOOKUP_KEY
import com.example.presentation.ui.fragments.CONTACT_LIST_FRAGMENT_TAG
import com.example.presentation.ui.fragments.ContactListFragment
import com.example.presentation.ui.interfaces.DialogFragmentGatewayOwner
import com.example.presentation.ui.interfaces.FragmentStackGateway
import com.example.presentation.ui.interfaces.FragmentStackGatewayOwner
import com.example.presentation.ui.interfaces.PermissionGateway
import com.example.presentation.ui.interfaces.PermissionGatewayOwner

class MainActivity :
    AppCompatActivity(),
    FragmentStackGatewayOwner,
    PermissionGatewayOwner,
    DialogFragmentGatewayOwner {
    override val permissionGateway: PermissionGateway
    override val stackGateway: FragmentStackGateway
    override val dialogFragmentGateway = DialogFragmentGatewayImpl(supportFragmentManager)

    private val transactionHelper = FragmentTransactionHelper(
        supportFragmentManager,
        R.id.mainActivityFragmentLayout
    )

    init {
        stackGateway = FragmentStackGatewayImpl(transactionHelper)
        permissionGateway = PermissionGatewayImpl(this, stackGateway, dialogFragmentGateway)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()
        requireFragmentBackStackNotEmpty(savedInstanceState)

        intent?.getStringExtra(CONTACT_ARG_LOOKUP_KEY)?.let {
            stackGateway.onCardClick(it)
        }
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
                    }
                )
        }
    }

    private fun requireFragmentBackStackNotEmpty(savedInstanceState: Bundle?) {
        val firstCreation = savedInstanceState == null

        if (firstCreation) {
            transactionHelper.changeFragmentWithTag(
                ContactListFragment(),
                CONTACT_LIST_FRAGMENT_TAG
            )
        }
    }
}

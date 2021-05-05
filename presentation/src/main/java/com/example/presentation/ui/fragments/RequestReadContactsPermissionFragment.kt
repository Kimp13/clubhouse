package com.example.presentation.ui.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.presentation.R
import com.example.presentation.databinding.FragmentRequestReadContactsPermissionBinding
import com.example.presentation.ui.interfaces.PoppableBackStackOwner
import com.example.presentation.ui.interfaces.ReadContactsPermissionRequester

const val REQUEST_READ_CONTACTS_PERMISSION_FRAGMENT_TAG =
    "fragment_request_read_contacts_permission"

class RequestReadContactsPermissionFragment : Fragment(
    R.layout.fragment_request_read_contacts_permission
) {
    private var backStackOwner: PoppableBackStackOwner? = null
    private var permissionRequester:
            ReadContactsPermissionRequester? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is PoppableBackStackOwner) {
            backStackOwner = context
        }

        if (context is ReadContactsPermissionRequester) {
            permissionRequester = context
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.run {
            supportActionBar?.run {
                setDisplayHomeAsUpEnabled(false)
                setTitle(R.string.we_beg_you)
            }

            initButton(view, this)
        }
    }

    override fun onStart() {
        super.onStart()

        if (permissionRequester?.checkContactPermission() == true) {
            backStackOwner?.popBackStack()
        }
    }

    override fun onDetach() {
        backStackOwner = null
        permissionRequester = null

        super.onDetach()
    }

    private fun initButton(view: View, context: Context) {
        FragmentRequestReadContactsPermissionBinding
            .bind(view)
            .run {
                requestPermissionsButton.setOnClickListener {
                    startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        ).apply {
                            data = Uri.fromParts(
                                "package",
                                context.packageName,
                                null
                            )
                        }
                    )
                }
            }
    }
}
package com.example.presentation.ui.activities.gateways

import androidx.fragment.app.FragmentManager
import com.example.presentation.ui.fragments.GeneralDialogFragment
import com.example.presentation.ui.fragments.RequestPermissionDialogFragment
import com.example.presentation.ui.interfaces.DialogFragmentGateway

class DialogFragmentGatewayImpl(
    private val fragmentManager: FragmentManager
) : DialogFragmentGateway {
    override fun requestPermission() {
        RequestPermissionDialogFragment().show(fragmentManager, null)
    }

    override fun showGeneralDialog(message: Int, positiveButtonText: Int) {
        GeneralDialogFragment.newInstance(message, positiveButtonText)
            .show(fragmentManager, null)
    }
}

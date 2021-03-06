package com.example.presentation.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.presentation.R
import com.example.presentation.ui.interfaces.PermissionGateway
import com.example.presentation.ui.interfaces.PermissionGatewayOwner

class RequestPermissionDialogFragment : DialogFragment() {
    private var gateway: PermissionGateway? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is PermissionGatewayOwner) {
            gateway = context.permissionGateway
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog
            .Builder(requireActivity())
            .setMessage(R.string.request_contact_read_permission)
            .setPositiveButton(R.string.ok, null)
            .create()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        gateway?.onRequestDialogDismissed()
    }

    override fun onDetach() {
        gateway = null

        super.onDetach()
    }
}

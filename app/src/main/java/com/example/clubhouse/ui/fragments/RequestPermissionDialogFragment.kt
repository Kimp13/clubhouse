package com.example.clubhouse.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.clubhouse.R
import com.example.clubhouse.ui.interfaces.RequestPermissionDialogDismissListener

class RequestPermissionDialogFragment : DialogFragment() {
    private var listener:
            RequestPermissionDialogDismissListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is RequestPermissionDialogDismissListener) {
            listener = context
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

        listener?.onRequestDialogDismissed()
    }

    override fun onDetach() {
        listener = null

        super.onDetach()
    }
}
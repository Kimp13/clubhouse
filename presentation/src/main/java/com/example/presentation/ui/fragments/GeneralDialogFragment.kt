package com.example.presentation.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import com.example.presentation.R

private const val MESSAGE_ARG = "dialog_message"
private const val POSITIVE_BUTTON_TEXT_ARG = "dialog_positive_button_text"

class GeneralDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(activity)

        val message = arguments?.getInt(MESSAGE_ARG)
            ?.takeUnless { it == 0 }
        message?.let {
            dialogBuilder.setMessage(it)
        }

        val positiveButtonText = arguments?.getInt(POSITIVE_BUTTON_TEXT_ARG)
            ?.takeUnless { it == 0 }
        positiveButtonText?.let {
            dialogBuilder.setPositiveButton(it, null)
        }

        return dialogBuilder.create()
    }

    companion object {
        fun newInstance(
            @StringRes
            message: Int,

            @StringRes
            positiveButtonText: Int = R.string.ok
        ) = GeneralDialogFragment().apply {
            arguments = Bundle().apply {
                putInt(MESSAGE_ARG, message)
                putInt(POSITIVE_BUTTON_TEXT_ARG, positiveButtonText)
            }
        }
    }
}

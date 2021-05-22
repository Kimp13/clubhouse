package com.example.presentation.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.presentation.R

class ClarifyMapControlsFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog
            .Builder(requireActivity())
            .setMessage(R.string.clarify_card_controls_text)
            .setPositiveButton(R.string.clarify_card_controls_ok, null)
            .create()
    }
}

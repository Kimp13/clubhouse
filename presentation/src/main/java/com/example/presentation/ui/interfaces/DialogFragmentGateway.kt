package com.example.presentation.ui.interfaces

import androidx.annotation.StringRes
import com.example.presentation.R

interface DialogFragmentGateway {
    fun showGeneralDialog(
        @StringRes
        message: Int,

        @StringRes
        positiveButtonText: Int = R.string.ok
    )

    fun requestPermission()
}

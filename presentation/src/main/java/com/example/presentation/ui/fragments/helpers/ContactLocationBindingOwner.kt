package com.example.presentation.ui.fragments.helpers

import android.view.View
import com.example.presentation.R
import com.example.presentation.databinding.FragmentContactLocationBinding

class ContactLocationBindingOwner(private val binding: FragmentContactLocationBinding) {
    fun noAddressAvailable() = binding.run {
        locationDescription.visibility = View.VISIBLE
        submit.visibility = View.GONE
        progressBar.visibility = View.GONE

        locationDescription.text = locationDescription.context
            .getString(R.string.no_location_set)
    }

    fun showProgress() = binding.run {
        locationDescription.visibility = View.GONE
        submit.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    fun setLocationDescription(description: String) = binding.run {
        stopShowingProgress()

        locationDescription.text = description
    }

    fun setOnSubmitListener(listener: View.OnClickListener) {
        binding.submit.setOnClickListener(listener)
    }

    private fun stopShowingProgress() = binding.run {
        locationDescription.visibility = View.VISIBLE
        submit.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }
}

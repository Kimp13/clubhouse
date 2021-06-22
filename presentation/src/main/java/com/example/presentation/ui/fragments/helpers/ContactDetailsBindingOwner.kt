package com.example.presentation.ui.fragments.helpers

import android.view.View
import android.widget.CompoundButton
import androidx.core.view.children
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.domain.entities.ContactEntity
import com.example.presentation.R
import com.example.presentation.databinding.FragmentContactDetailsBinding

class ContactDetailsBindingOwner(private val binding: FragmentContactDetailsBinding) {
    private val delegate = ContactDetailsUpdateDelegate(binding)

    fun initializeRefreshViewWithRefreshListener(
        listener: SwipeRefreshLayout.OnRefreshListener
    ) = binding.refreshView.run {
        setColorSchemeResources(R.color.colorPrimary)
        setOnRefreshListener(listener)
        isEnabled = false
    }

    fun updateContactDetails(contact: ContactEntity) {
        delegate.updateRefreshView()
        delegate.updatePhoto(contact.photoId)
        delegate.updateBirthDate(contact.birthDate)
        delegate.updateLocation(contact.location)
        delegate.updateTextViews(contact)
    }

    fun setHasBirthdayNotificationsOn(has: Boolean) {
        binding.remindSwitch.isChecked = has
    }

    fun setBirthdaySubscriptionChangeListener(listener: CompoundButton.OnCheckedChangeListener) {
        binding.remindSwitch.setOnCheckedChangeListener(listener)
    }

    fun showRefreshing() {
        binding.refreshView.isRefreshing = true
    }

    fun stopShowingRefreshing() {
        binding.refreshView.isRefreshing = false
    }

    fun setEditLocationListener(listener: View.OnClickListener) {
        binding.editLocation.setOnClickListener(listener)
    }

    fun setViewLocationListener(listener: View.OnClickListener) {
        binding.viewLocation.setOnClickListener(listener)
    }

    fun setNavigateListener(listener: View.OnClickListener) {
        binding.navigate.setOnClickListener(listener)
    }

    fun showError() {
        binding.contents.children.forEach {
            it.visibility = View.GONE
        }
        binding.errorGroup.visibility = View.VISIBLE
    }
}

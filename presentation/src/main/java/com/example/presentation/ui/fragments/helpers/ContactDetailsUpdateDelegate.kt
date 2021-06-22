package com.example.presentation.ui.fragments.helpers

import android.content.res.ColorStateList
import android.text.format.DateUtils
import android.view.View
import androidx.core.content.ContextCompat
import com.example.domain.entities.BirthDate
import com.example.domain.entities.ContactEntity
import com.example.domain.entities.ContactLocation
import com.example.presentation.R
import com.example.presentation.databinding.FragmentContactDetailsBinding
import com.example.presentation.ui.views.fillViewsWithContents
import com.example.presentation.ui.views.hideAll
import com.example.presentation.ui.views.showAll

class ContactDetailsUpdateDelegate(private val binding: FragmentContactDetailsBinding) {
    private val photoHelper = ContactPhotoHelper()

    fun updateRefreshView() = binding.run {
        val refreshViewInactive = !refreshView.isEnabled

        if (refreshViewInactive) {
            refreshView.isEnabled = true
            contents.removeView(progressGroup)
        }
    }

    fun updatePhoto(photoId: Long?) = binding.photo.run {
        visibility = View.VISIBLE

        if (photoId == null) {
            setImageResource(R.drawable.ic_baseline_person_24)
            imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.colorPrimary)
            )
        } else {
            setImageURI(photoHelper.makePhotoUri(photoId))
            imageTintList = null
        }
    }

    fun updateBirthDate(birthDateObject: BirthDate?) = binding.run {
        val birthDateView = birthDate

        if (birthDateObject == null) {
            hideAll(birthDateView, clarifyRemind, remindSwitch)
        } else {
            showAll(birthDateView, clarifyRemind, remindSwitch)

            birthDateView.run {
                text = context.getString(
                    R.string.birthday_fmt,
                    DateUtils.formatDateTime(
                        context,
                        birthDateObject.timeInMillis,
                        DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_NO_YEAR
                    )
                )
            }
        }
    }

    fun updateLocation(location: ContactLocation?) = binding.run {
        showAll(locationDescription, editLocation)

        if (location == null) {
            locationDescription.text = locationDescription.context.getString(
                R.string.no_location_set
            )
            hideAll(viewLocation, navigate)
        } else {
            locationDescription.text = location.description
                ?: locationDescription.context.getString(
                    R.string.no_location_set
                )
            showAll(viewLocation, navigate)
        }
    }

    fun updateTextViews(contact: ContactEntity) = binding.run {
        showAll(name, description)
        name.text = contact.name ?: name.context.getString(R.string.no_name)
        description.text = contact.description ?: description.context.getString(
            R.string.no_description
        )

        contact.phones.fillViewsWithContents(phone1, phone2)
        contact.emails.fillViewsWithContents(email1, email2)
    }
}

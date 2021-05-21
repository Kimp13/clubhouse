package com.example.presentation.ui.viewmodels.states

import com.example.domain.entities.LocationEntity

data class ContactLocationState(
    val location: LocationEntity? = null,
    val address: String? = null,
    val progress: Boolean = false,
    val locationWritten: Boolean = false,
    val areMapControlsClarified: Boolean? = null
)

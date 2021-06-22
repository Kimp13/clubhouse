package com.example.presentation.ui.fragments.helpers

import com.example.domain.entities.LocationEntity
import com.example.presentation.data.entities.LocationWithDescription
import com.example.presentation.data.entities.ParcelableSimpleContact

class ContactRouteDelegate(
    val contacts: Pair<ParcelableSimpleContact, ParcelableSimpleContact>
) {
    fun getFirstPointWithDescription(list: List<LocationEntity>) = list.first().run {
        LocationWithDescription(
            latitude,
            longitude,
            contacts.first.name ?: ""
        )
    }

    fun getLastPointWithDescription(list: List<LocationEntity>) = list.last().run {
        LocationWithDescription(
            latitude,
            longitude,
            contacts.second.name ?: ""
        )
    }
}

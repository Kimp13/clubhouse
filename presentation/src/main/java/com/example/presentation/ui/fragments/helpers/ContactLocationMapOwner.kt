package com.example.presentation.ui.fragments.helpers

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

private const val BELOVED_COMPANY_LATITUDE = 56.8463985
private const val BELOVED_COMPANY_LONGITUDE = 53.2332288

class ContactLocationMapOwner(
    private val map: GoogleMap?
) {
    fun setOnMapClickListener(listener: GoogleMap.OnMapClickListener) {
        map?.setOnMapClickListener(listener)
    }

    fun centerMap(point: LatLng?) {
        val shownPoint = point ?: LatLng(BELOVED_COMPANY_LATITUDE, BELOVED_COMPANY_LONGITUDE)

        map?.run {
            moveCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder()
                        .target(shownPoint)
                        .zoom(maxZoomLevel + MAP_CAMERA_ZOOM_TERM)
                        .build()
                )
            )
        }
    }

    fun changeSelectedPoint(point: LatLng?) {
        map?.clear()

        if (point != null) {
            addMarker(point)
        }
    }

    fun addMarker(point: LatLng) {
        map?.addMarker(MarkerOptions().position(point))
    }
}

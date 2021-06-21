package com.example.presentation.ui.fragments.helpers

import com.example.presentation.data.entities.LocationWithDescription
import com.example.presentation.data.entities.toLatLng
import com.example.presentation.ui.fragments.interfaces.GoogleMapHelpee
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.math.min

const val MAP_CAMERA_ZOOM_TERM = -7.5F

class GoogleMapHelper(
    private val helpee: GoogleMapHelpee
) : OnMapReadyCallback {
    var map: GoogleMap? = null

    override fun onMapReady(map: GoogleMap?) {
        this.map = map

        helpee.onMapReady()
    }

    fun addAndCenterMarkersWithDescription(
        points: List<LocationWithDescription>
    ) {
        map?.clear()

        val boundsBuilder = LatLngBounds.builder()

        points.forEach {
            boundsBuilder.include(it.toLatLng())
            addMarkerWithDescription(it)
        }

        moveCameraWithBounds(boundsBuilder.build())
        requireZoomNotTooClose()
    }

    fun addMarkerWithDescription(
        point: LocationWithDescription
    ) {
        val markerOptions = MarkerOptions().position(point.toLatLng())
            .title(point.description)

        map?.cameraPosition

        map?.addMarker(markerOptions)
    }

    private fun moveCameraWithBounds(bounds: LatLngBounds) {
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                helpee.getMapPadding()
            )
        )
    }

    private fun requireZoomNotTooClose() = map?.run {
        val appropriateZoomLevel = maxZoomLevel + MAP_CAMERA_ZOOM_TERM
        val currentZoom = cameraPosition.zoom
        val zoomToSet = min(appropriateZoomLevel, currentZoom)

        map?.moveCamera(CameraUpdateFactory.zoomTo(zoomToSet))
    }
}

package com.example.presentation.ui.fragments.helpers

import com.example.presentation.data.entities.LocationWithDescription
import com.example.presentation.data.entities.toLatLng
import com.example.presentation.ui.fragments.interfaces.GoogleMapHelpee
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlin.math.min

const val MAP_CAMERA_ZOOM_TERM = -7.5F

class GoogleMapHelper(
    private val map: GoogleMap?,
    private val helpee: GoogleMapHelpee
) {
    fun addAndCenterMarkersWithDescription(points: List<LocationWithDescription>) {
        map?.clear()

        fitAndCenterLocations(points)

        points.forEach {
            addMarkerWithDescription(it)
        }
    }

    fun buildRouteBetweenPoints(
        startPoint: LocationWithDescription,
        endPoint: LocationWithDescription,
        allPoints: List<LatLng>
    ) {
        map?.clear()

        addMarkerWithDescription(startPoint)
        addMarkerWithDescription(endPoint)
        fitAndCenterLatLngs(allPoints)

        var polylineOptions = PolylineOptions()

        allPoints.forEach {
            polylineOptions = polylineOptions.add(it)
        }

        map?.addPolyline(polylineOptions)
    }

    fun addMarkerWithDescription(point: LocationWithDescription) {
        val markerOptions = MarkerOptions().position(point.toLatLng())
            .title(point.description)

        map?.addMarker(markerOptions)
    }

    private fun fitAndCenterLocations(points: List<LocationWithDescription>) = buildBounds {
        points.forEach {
            include(it.toLatLng())
        }
    }

    private fun fitAndCenterLatLngs(points: List<LatLng>) = buildBounds {
        points.forEach {
            include(it)
        }
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

        moveCamera(CameraUpdateFactory.zoomTo(zoomToSet))
    }

    private fun buildBounds(block: LatLngBounds.Builder.() -> Unit) {
        val boundsBuilder = LatLngBounds.builder()

        block(boundsBuilder)

        moveCameraWithBounds(boundsBuilder.build())
        requireZoomNotTooClose()
    }
}

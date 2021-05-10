package com.example.presentation.data.entities

data class DirectionsResponsePolyline(
    val points: String
)

data class DirectionsResponseStep(
    val polyline: DirectionsResponsePolyline
)

data class DirectionsResponseLeg(
    val steps: List<DirectionsResponseStep>
)

data class DirectionsResponseRoute(
    val legs: List<DirectionsResponseLeg>
)

data class DirectionsResponse(
    val routes: List<DirectionsResponseRoute>
)
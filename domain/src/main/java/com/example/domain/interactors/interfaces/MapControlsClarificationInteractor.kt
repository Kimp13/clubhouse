package com.example.domain.interactors.interfaces

interface MapControlsClarificationInteractor {
    suspend fun areMapControlsClarified(): Boolean
    suspend fun writeMapControlsClarified(): Boolean
}

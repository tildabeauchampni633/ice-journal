package com.Faxzoku9.Qozcud.domain.model

data class TripResult(
    val lakeId: Int,
    val lureId: Int,
    val casts: Int,
    val landed: Int,
    val points: Int,
    val bestCatch: Catch?,
    val deepestLandedM: Float,
    val escapedSpeciesId: Int,
    val endedEarly: Boolean,
    val snapped: Boolean,
    val newSpeciesId: Int,
    val newBestPoints: Boolean
)

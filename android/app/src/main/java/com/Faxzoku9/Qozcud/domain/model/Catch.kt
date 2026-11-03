package com.Faxzoku9.Qozcud.domain.model

data class Catch(
    val speciesId: Int,
    val weightKg: Float,
    val depthM: Float,
    val points: Int,
    val lakeId: Int,
    val conditions: Conditions
)

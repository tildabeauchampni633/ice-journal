package com.Faxzoku9.Qozcud.domain.model

data class Species(
    val id: Int,
    val nameRes: Int,
    val noteRes: Int,
    val drawableRes: Int,
    val minDepthM: Float,
    val maxDepthM: Float,
    val minWeightKg: Float,
    val maxWeightKg: Float,
    val basePoints: Int,
    val spriteDp: Int
)

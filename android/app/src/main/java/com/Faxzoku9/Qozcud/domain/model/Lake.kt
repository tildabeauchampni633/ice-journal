package com.Faxzoku9.Qozcud.domain.model

data class Lake(
    val id: Int,
    val nameRes: Int,
    val maxDepthM: Float,
    val unlockPoints: Int,
    val speciesIds: List<Int>,
    val primeMinM: Float,
    val primeMaxM: Float
)

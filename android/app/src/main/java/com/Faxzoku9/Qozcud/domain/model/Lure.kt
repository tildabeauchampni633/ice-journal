package com.Faxzoku9.Qozcud.domain.model

data class Lure(
    val id: Int,
    val nameRes: Int,
    val tradeOffRes: Int,
    val biteWindowMs: Long,
    val safeMin: Int,
    val safeMax: Int,
    val pointsMultiplier: Float,
    val unlockPoints: Int
)

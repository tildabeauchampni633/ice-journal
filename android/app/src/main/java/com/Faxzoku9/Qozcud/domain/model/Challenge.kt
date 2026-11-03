package com.Faxzoku9.Qozcud.domain.model

data class Challenge(
    val id: Int,
    val titleRes: Int,
    val descriptionRes: Int,
    val rewardPoints: Int,
    val completed: Boolean
)

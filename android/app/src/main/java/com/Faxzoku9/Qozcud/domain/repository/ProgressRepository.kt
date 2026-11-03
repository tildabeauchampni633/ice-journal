package com.Faxzoku9.Qozcud.domain.repository

import com.Faxzoku9.Qozcud.domain.model.TripResult

interface ProgressRepository {
    fun lifetimePoints(): Int

    fun bestTripPoints(): Int

    fun totalCatches(): Int

    fun tripIndex(): Int

    fun selectedLakeId(): Int

    fun selectedLureId(): Int

    fun selectLake(lakeId: Int)

    fun selectLure(lureId: Int)

    fun dayOfYear(): Int

    fun isChallengeDone(challengeId: Int, dayOfYear: Int): Boolean

    fun markChallengeDone(challengeId: Int, dayOfYear: Int)

    fun saveTrip(result: TripResult)

    fun lastTrip(): TripResult?
}

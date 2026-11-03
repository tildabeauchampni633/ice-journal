package com.Faxzoku9.Qozcud.domain.usecase

import com.Faxzoku9.Qozcud.domain.model.Challenge
import com.Faxzoku9.Qozcud.domain.model.TripResult

class EvaluateChallengesUseCase {

    operator fun invoke(
        templates: List<Challenge>,
        dayOfYear: Int,
        trip: TripResult?
    ): List<Challenge> = templates.mapIndexed { index, template ->
        template.copy(
            rewardPoints = template.rewardPoints + (dayOfYear + index * 7) % 4 * 5,
            completed = trip != null && isDone(template.id, trip)
        )
    }

    fun rewardFor(templates: List<Challenge>, dayOfYear: Int, trip: TripResult): Int =
        invoke(templates, dayOfYear, trip).filter { it.completed }.sumOf { it.rewardPoints }

    private fun isDone(challengeId: Int, trip: TripResult): Boolean = when (challengeId) {
        CHALLENGE_DEEP_WATER -> trip.landed > 0 && trip.deepestLandedM > 10.0f
        CHALLENGE_STEADY_HANDS -> trip.landed > 0 && !trip.snapped
        CHALLENGE_FIELD_WORK -> trip.newSpeciesId >= 0
        else -> false
    }

    companion object {
        const val CHALLENGE_DEEP_WATER = 1
        const val CHALLENGE_STEADY_HANDS = 2
        const val CHALLENGE_FIELD_WORK = 3
    }
}

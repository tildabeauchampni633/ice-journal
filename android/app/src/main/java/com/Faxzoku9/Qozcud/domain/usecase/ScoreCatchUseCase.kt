package com.Faxzoku9.Qozcud.domain.usecase

import com.Faxzoku9.Qozcud.domain.model.Lure
import com.Faxzoku9.Qozcud.domain.model.Species
import kotlin.math.roundToInt

class ScoreCatchUseCase {

    operator fun invoke(species: Species, weightKg: Float, lure: Lure): Int {
        val span = (species.maxWeightKg - species.minWeightKg).coerceAtLeast(0.01f)
        val sizeMultiplier = (1.0f + 0.8f * ((weightKg - species.minWeightKg) / span))
            .coerceIn(1.0f, 1.8f)
        return (species.basePoints * sizeMultiplier * lure.pointsMultiplier).roundToInt()
    }
}

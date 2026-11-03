package com.Faxzoku9.Qozcud.domain.usecase

import com.Faxzoku9.Qozcud.domain.model.Conditions
import com.Faxzoku9.Qozcud.domain.model.Lake
import com.Faxzoku9.Qozcud.domain.model.LightPhase
import com.Faxzoku9.Qozcud.domain.model.Species
import com.Faxzoku9.Qozcud.domain.model.Weather
import kotlin.random.Random

class PickSpeciesUseCase {

    operator fun invoke(
        lake: Lake,
        catalog: List<Species>,
        conditions: Conditions,
        seed: Int
    ): Species {
        val available = catalog.filter { lake.speciesIds.contains(it.id) }
        val pool = if (available.isEmpty()) catalog else available
        val weights = pool.map { weightFor(it, conditions) }
        val total = weights.sum()
        val random = Random(seed)
        var roll = random.nextDouble() * total
        for (index in pool.indices) {
            roll -= weights[index]
            if (roll <= 0.0) {
                return pool[index]
            }
        }
        return pool.last()
    }

    private fun weightFor(species: Species, conditions: Conditions): Double {
        var weight = 10.0
        val deep = species.minDepthM >= 10.0f
        val shallow = species.maxDepthM <= 7.0f
        if (conditions.weather == Weather.OVERCAST) {
            if (deep) weight *= 2.0
            if (shallow) weight *= 0.5
        }
        val lowLight = conditions.light == LightPhase.DAWN || conditions.light == LightPhase.DUSK
        if (lowLight && !deep && !shallow) {
            weight *= 2.0
        }
        return weight
    }
}

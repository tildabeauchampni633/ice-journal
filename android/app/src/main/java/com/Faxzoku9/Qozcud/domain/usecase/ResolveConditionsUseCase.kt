package com.Faxzoku9.Qozcud.domain.usecase

import com.Faxzoku9.Qozcud.domain.model.Conditions
import com.Faxzoku9.Qozcud.domain.model.LightPhase
import com.Faxzoku9.Qozcud.domain.model.Weather
import kotlin.random.Random

class ResolveConditionsUseCase {

    operator fun invoke(lakeId: Int, tripIndex: Int, castIndex: Int): Conditions {
        val random = Random(lakeId * 7919 + tripIndex * 31 + castIndex)
        val weathers = Weather.entries
        val lights = LightPhase.entries
        return Conditions(
            weather = weathers[random.nextInt(weathers.size)],
            light = lights[random.nextInt(lights.size)]
        )
    }
}

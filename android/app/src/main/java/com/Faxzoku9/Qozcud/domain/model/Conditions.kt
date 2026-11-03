package com.Faxzoku9.Qozcud.domain.model

enum class Weather { CLEAR, LIGHT_SNOW, OVERCAST }

enum class LightPhase { DAWN, BLUE_HOUR, DUSK }

data class Conditions(
    val weather: Weather,
    val light: LightPhase
) {
    val alwaysStrongEcho: Boolean
        get() = weather == Weather.CLEAR && light == LightPhase.BLUE_HOUR

    val weakEcho: Boolean
        get() = weather == Weather.LIGHT_SNOW

    val weightBonus: Float
        get() = if (weather == Weather.LIGHT_SNOW) 1.1f else 1.0f

    val biteWindowBonusMs: Long
        get() = if (light == LightPhase.DAWN || light == LightPhase.DUSK) 120L else 0L
}

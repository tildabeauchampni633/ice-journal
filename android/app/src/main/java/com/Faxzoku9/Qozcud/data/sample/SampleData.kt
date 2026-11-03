package com.Faxzoku9.Qozcud.data.sample

import com.Faxzoku9.Qozcud.R
import com.Faxzoku9.Qozcud.domain.model.Challenge
import com.Faxzoku9.Qozcud.domain.model.Lake
import com.Faxzoku9.Qozcud.domain.model.LightPhase
import com.Faxzoku9.Qozcud.domain.model.Lure
import com.Faxzoku9.Qozcud.domain.model.Species
import com.Faxzoku9.Qozcud.domain.model.Weather
import com.Faxzoku9.Qozcud.domain.usecase.EvaluateChallengesUseCase

object SampleData {

    const val SPECIES_PERCH = 1
    const val SPECIES_CHAR = 2
    const val SPECIES_PIKE = 3

    val species: List<Species> = listOf(
        Species(
            id = SPECIES_PERCH,
            nameRes = R.string.species_perch,
            noteRes = R.string.note_perch,
            drawableRes = R.drawable.sprite_1,
            minDepthM = 2.5f,
            maxDepthM = 6.0f,
            minWeightKg = 0.3f,
            maxWeightKg = 0.9f,
            basePoints = 10,
            spriteDp = 44
        ),
        Species(
            id = SPECIES_CHAR,
            nameRes = R.string.species_char,
            noteRes = R.string.note_char,
            drawableRes = R.drawable.sprite_5,
            minDepthM = 6.5f,
            maxDepthM = 11.0f,
            minWeightKg = 1.2f,
            maxWeightKg = 3.4f,
            basePoints = 25,
            spriteDp = 58
        ),
        Species(
            id = SPECIES_PIKE,
            nameRes = R.string.species_pike,
            noteRes = R.string.note_pike,
            drawableRes = R.drawable.sprite_2,
            minDepthM = 11.5f,
            maxDepthM = 17.0f,
            minWeightKg = 3.5f,
            maxWeightKg = 8.2f,
            basePoints = 45,
            spriteDp = 72
        )
    )

    val lakes: List<Lake> = listOf(
        Lake(
            id = 0,
            nameRes = R.string.lake_still_hollow,
            maxDepthM = 12.0f,
            unlockPoints = 0,
            speciesIds = listOf(SPECIES_PERCH, SPECIES_CHAR),
            primeMinM = 3.0f,
            primeMaxM = 7.0f
        ),
        Lake(
            id = 1,
            nameRes = R.string.lake_birch_narrows,
            maxDepthM = 15.0f,
            unlockPoints = 120,
            speciesIds = listOf(SPECIES_PERCH, SPECIES_CHAR, SPECIES_PIKE),
            primeMinM = 6.0f,
            primeMaxM = 11.0f
        ),
        Lake(
            id = 2,
            nameRes = R.string.lake_frostmere_deep,
            maxDepthM = 18.0f,
            unlockPoints = 340,
            speciesIds = listOf(SPECIES_CHAR, SPECIES_PIKE),
            primeMinM = 10.0f,
            primeMaxM = 16.0f
        )
    )

    val lures: List<Lure> = listOf(
        Lure(
            id = 0,
            nameRes = R.string.lure_copper_spoon,
            tradeOffRes = R.string.lure_trade_copper,
            biteWindowMs = 900L,
            safeMin = 30,
            safeMax = 78,
            pointsMultiplier = 1.00f,
            unlockPoints = 0
        ),
        Lure(
            id = 1,
            nameRes = R.string.lure_glow_jig,
            tradeOffRes = R.string.lure_trade_glow,
            biteWindowMs = 1150L,
            safeMin = 34,
            safeMax = 72,
            pointsMultiplier = 1.00f,
            unlockPoints = 80
        ),
        Lure(
            id = 2,
            nameRes = R.string.lure_minnow_rig,
            tradeOffRes = R.string.lure_trade_minnow,
            biteWindowMs = 760L,
            safeMin = 26,
            safeMax = 84,
            pointsMultiplier = 1.15f,
            unlockPoints = 220
        )
    )

    val challengeTemplates: List<Challenge> = listOf(
        Challenge(
            id = EvaluateChallengesUseCase.CHALLENGE_DEEP_WATER,
            titleRes = R.string.challenge_deep_title,
            descriptionRes = R.string.challenge_deep_desc,
            rewardPoints = 20,
            completed = false
        ),
        Challenge(
            id = EvaluateChallengesUseCase.CHALLENGE_STEADY_HANDS,
            titleRes = R.string.challenge_steady_title,
            descriptionRes = R.string.challenge_steady_desc,
            rewardPoints = 15,
            completed = false
        ),
        Challenge(
            id = EvaluateChallengesUseCase.CHALLENGE_FIELD_WORK,
            titleRes = R.string.challenge_field_title,
            descriptionRes = R.string.challenge_field_desc,
            rewardPoints = 30,
            completed = false
        )
    )

    fun lakeById(id: Int): Lake = lakes.firstOrNull { it.id == id } ?: lakes.first()

    fun lureById(id: Int): Lure = lures.firstOrNull { it.id == id } ?: lures.first()

    fun speciesById(id: Int): Species = species.firstOrNull { it.id == id } ?: species.first()

    fun weatherLabel(weather: Weather): Int = when (weather) {
        Weather.CLEAR -> R.string.weather_clear
        Weather.LIGHT_SNOW -> R.string.weather_light_snow
        Weather.OVERCAST -> R.string.weather_overcast
    }

    fun lightLabel(light: LightPhase): Int = when (light) {
        LightPhase.DAWN -> R.string.light_dawn
        LightPhase.BLUE_HOUR -> R.string.light_blue_hour
        LightPhase.DUSK -> R.string.light_dusk
    }
}

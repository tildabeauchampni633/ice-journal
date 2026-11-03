package com.Faxzoku9.Qozcud.data.repository

import com.Faxzoku9.Qozcud.data.local.JournalPreferences
import com.Faxzoku9.Qozcud.domain.model.Catch
import com.Faxzoku9.Qozcud.domain.model.Conditions
import com.Faxzoku9.Qozcud.domain.model.LightPhase
import com.Faxzoku9.Qozcud.domain.model.TripResult
import com.Faxzoku9.Qozcud.domain.model.Weather
import com.Faxzoku9.Qozcud.domain.repository.ProgressRepository
import java.util.Calendar

class ProgressRepositoryImpl(
    private val preferences: JournalPreferences
) : ProgressRepository {

    override fun lifetimePoints(): Int =
        preferences.readInt(JournalPreferences.KEY_LIFETIME_POINTS, 0)

    override fun bestTripPoints(): Int =
        preferences.readInt(JournalPreferences.KEY_BEST_TRIP_POINTS, 0)

    override fun totalCatches(): Int =
        preferences.readInt(JournalPreferences.KEY_TOTAL_CATCHES, 0)

    override fun tripIndex(): Int =
        preferences.readInt(JournalPreferences.KEY_TRIP_INDEX, 0)

    override fun selectedLakeId(): Int =
        preferences.readInt(JournalPreferences.KEY_SELECTED_LAKE, 0)

    override fun selectedLureId(): Int =
        preferences.readInt(JournalPreferences.KEY_SELECTED_LURE, 0)

    override fun selectLake(lakeId: Int) {
        preferences.writeInt(JournalPreferences.KEY_SELECTED_LAKE, lakeId)
    }

    override fun selectLure(lureId: Int) {
        preferences.writeInt(JournalPreferences.KEY_SELECTED_LURE, lureId)
    }

    override fun dayOfYear(): Int = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

    override fun isChallengeDone(challengeId: Int, dayOfYear: Int): Boolean =
        preferences.readInt(challengeKey(challengeId), -1) == dayOfYear

    override fun markChallengeDone(challengeId: Int, dayOfYear: Int) {
        preferences.writeInt(challengeKey(challengeId), dayOfYear)
    }

    override fun saveTrip(result: TripResult) {
        preferences.writeInt(
            JournalPreferences.KEY_LIFETIME_POINTS,
            lifetimePoints() + result.points
        )
        if (result.points > bestTripPoints()) {
            preferences.writeInt(JournalPreferences.KEY_BEST_TRIP_POINTS, result.points)
        }
        preferences.writeInt(
            JournalPreferences.KEY_TOTAL_CATCHES,
            totalCatches() + result.landed
        )
        preferences.writeInt(JournalPreferences.KEY_TRIP_INDEX, tripIndex() + 1)
        preferences.writeString(JournalPreferences.KEY_LAST_TRIP, encode(result))
    }

    override fun lastTrip(): TripResult? =
        decode(preferences.readString(JournalPreferences.KEY_LAST_TRIP))

    private fun challengeKey(challengeId: Int): String =
        JournalPreferences.PREFIX_CHALLENGE + challengeId + JournalPreferences.SUFFIX_DAY

    private fun encode(result: TripResult): String {
        val best = result.bestCatch
        val parts = listOf(
            result.lakeId.toString(),
            result.lureId.toString(),
            result.casts.toString(),
            result.landed.toString(),
            result.points.toString(),
            result.deepestLandedM.toString(),
            result.escapedSpeciesId.toString(),
            if (result.endedEarly) "1" else "0",
            if (result.snapped) "1" else "0",
            result.newSpeciesId.toString(),
            if (result.newBestPoints) "1" else "0",
            (best?.speciesId ?: -1).toString(),
            (best?.weightKg ?: 0f).toString(),
            (best?.depthM ?: 0f).toString(),
            (best?.points ?: 0).toString(),
            (best?.lakeId ?: 0).toString(),
            (best?.conditions?.weather?.ordinal ?: 0).toString(),
            (best?.conditions?.light?.ordinal ?: 0).toString()
        )
        return parts.joinToString(SEPARATOR)
    }

    private fun decode(raw: String?): TripResult? {
        if (raw.isNullOrBlank()) {
            return null
        }
        val parts = raw.split(SEPARATOR)
        if (parts.size < FIELD_COUNT) {
            return null
        }
        val bestSpecies = parts[11].toIntOrNull() ?: -1
        val best = if (bestSpecies >= 0) {
            Catch(
                speciesId = bestSpecies,
                weightKg = parts[12].toFloatOrNull() ?: 0f,
                depthM = parts[13].toFloatOrNull() ?: 0f,
                points = parts[14].toIntOrNull() ?: 0,
                lakeId = parts[15].toIntOrNull() ?: 0,
                conditions = Conditions(
                    weather = Weather.entries[
                        (parts[16].toIntOrNull() ?: 0).coerceIn(0, Weather.entries.size - 1)
                    ],
                    light = LightPhase.entries[
                        (parts[17].toIntOrNull() ?: 0).coerceIn(0, LightPhase.entries.size - 1)
                    ]
                )
            )
        } else {
            null
        }
        return TripResult(
            lakeId = parts[0].toIntOrNull() ?: 0,
            lureId = parts[1].toIntOrNull() ?: 0,
            casts = parts[2].toIntOrNull() ?: 0,
            landed = parts[3].toIntOrNull() ?: 0,
            points = parts[4].toIntOrNull() ?: 0,
            bestCatch = best,
            deepestLandedM = parts[5].toFloatOrNull() ?: 0f,
            escapedSpeciesId = parts[6].toIntOrNull() ?: -1,
            endedEarly = parts[7] == "1",
            snapped = parts[8] == "1",
            newSpeciesId = parts[9].toIntOrNull() ?: -1,
            newBestPoints = parts[10] == "1"
        )
    }

    private companion object {
        const val SEPARATOR = ";"
        const val FIELD_COUNT = 18
    }
}

package com.Faxzoku9.Qozcud.data.repository

import com.Faxzoku9.Qozcud.data.local.JournalPreferences
import com.Faxzoku9.Qozcud.data.sample.SampleData
import com.Faxzoku9.Qozcud.domain.model.JournalEntry
import com.Faxzoku9.Qozcud.domain.repository.JournalRepository

class JournalRepositoryImpl(
    private val preferences: JournalPreferences
) : JournalRepository {

    override fun entries(): List<JournalEntry> = SampleData.species.map { entryFor(it.id) }

    override fun entryFor(speciesId: Int): JournalEntry {
        val best = preferences.readFloat(bestKey(speciesId), 0f)
        return JournalEntry(
            speciesId = speciesId,
            discovered = best > 0f,
            bestWeightKg = best,
            lakeId = preferences.readInt(lakeKey(speciesId), 0)
        )
    }

    override fun discoveredCount(): Int = entries().count { it.discovered }

    override fun recordCatch(speciesId: Int, weightKg: Float, lakeId: Int): Boolean {
        val previous = preferences.readFloat(bestKey(speciesId), 0f)
        val isNewSpecies = previous <= 0f
        if (weightKg > previous) {
            preferences.writeFloat(bestKey(speciesId), weightKg)
            preferences.writeInt(lakeKey(speciesId), lakeId)
        }
        return isNewSpecies
    }

    private fun bestKey(speciesId: Int): String =
        JournalPreferences.PREFIX_SPECIES_BEST + speciesId + JournalPreferences.SUFFIX_BEST_KG

    private fun lakeKey(speciesId: Int): String =
        JournalPreferences.PREFIX_SPECIES_BEST + speciesId + JournalPreferences.SUFFIX_LAKE
}

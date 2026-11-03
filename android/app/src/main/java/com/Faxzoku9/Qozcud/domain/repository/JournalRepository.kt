package com.Faxzoku9.Qozcud.domain.repository

import com.Faxzoku9.Qozcud.domain.model.JournalEntry

interface JournalRepository {
    fun entries(): List<JournalEntry>

    fun entryFor(speciesId: Int): JournalEntry

    fun discoveredCount(): Int

    fun recordCatch(speciesId: Int, weightKg: Float, lakeId: Int): Boolean
}

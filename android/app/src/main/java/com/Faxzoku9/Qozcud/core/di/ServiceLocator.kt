package com.Faxzoku9.Qozcud.core.di

import android.content.Context
import com.Faxzoku9.Qozcud.data.local.JournalPreferences
import com.Faxzoku9.Qozcud.data.repository.JournalRepositoryImpl
import com.Faxzoku9.Qozcud.data.repository.ProgressRepositoryImpl
import com.Faxzoku9.Qozcud.domain.repository.JournalRepository
import com.Faxzoku9.Qozcud.domain.repository.ProgressRepository
import com.Faxzoku9.Qozcud.domain.usecase.EvaluateChallengesUseCase
import com.Faxzoku9.Qozcud.domain.usecase.PickSpeciesUseCase
import com.Faxzoku9.Qozcud.domain.usecase.ResolveConditionsUseCase
import com.Faxzoku9.Qozcud.domain.usecase.ScoreCatchUseCase

object ServiceLocator {

    private var preferences: JournalPreferences? = null

    private val store: JournalPreferences
        get() = requireNotNull(preferences)

    val journalRepository: JournalRepository by lazy { JournalRepositoryImpl(store) }

    val progressRepository: ProgressRepository by lazy { ProgressRepositoryImpl(store) }

    val resolveConditions: ResolveConditionsUseCase by lazy { ResolveConditionsUseCase() }

    val pickSpecies: PickSpeciesUseCase by lazy { PickSpeciesUseCase() }

    val scoreCatch: ScoreCatchUseCase by lazy { ScoreCatchUseCase() }

    val evaluateChallenges: EvaluateChallengesUseCase by lazy { EvaluateChallengesUseCase() }

    fun init(context: Context) {
        if (preferences == null) {
            preferences = JournalPreferences(context.applicationContext)
        }
    }
}

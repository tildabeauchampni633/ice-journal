package com.Faxzoku9.Qozcud.data.local

import android.content.Context
import android.content.SharedPreferences

class JournalPreferences(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)

    fun readInt(key: String, fallback: Int): Int = preferences.getInt(key, fallback)

    fun writeInt(key: String, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }

    fun readFloat(key: String, fallback: Float): Float = preferences.getFloat(key, fallback)

    fun writeFloat(key: String, value: Float) {
        preferences.edit().putFloat(key, value).apply()
    }

    fun readString(key: String): String? = preferences.getString(key, null)

    fun writeString(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    companion object {
        const val STORE_NAME = "ice_journal_prefs"
        const val KEY_LIFETIME_POINTS = "lifetime_points"
        const val KEY_BEST_TRIP_POINTS = "best_trip_points"
        const val KEY_TOTAL_CATCHES = "total_catches"
        const val KEY_TRIP_INDEX = "trip_index"
        const val KEY_SELECTED_LAKE = "selected_lake"
        const val KEY_SELECTED_LURE = "selected_lure"
        const val KEY_LAST_TRIP = "last_trip"
        const val PREFIX_SPECIES_BEST = "species_"
        const val SUFFIX_BEST_KG = "_best_kg"
        const val SUFFIX_LAKE = "_lake"
        const val PREFIX_CHALLENGE = "challenge_"
        const val SUFFIX_DAY = "_day"
    }
}

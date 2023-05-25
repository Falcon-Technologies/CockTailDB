package com.sofit.task.util

import androidx.preference.PreferenceManager
import com.sofit.AppClass

object PreferenceUtil {
    private val sharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(AppClass.getContext())

    var searchByAlphabet
        get() = sharedPreferences.getBoolean(SEARCH_BY_ALPHABET, false)
        set(value) {
            sharedPreferences.edit().apply {
                putBoolean(SEARCH_BY_ALPHABET, value)
                apply()
            }
        }
}

const val SEARCH_BY_ALPHABET = "search_by_alphabet"
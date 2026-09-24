package com.stepalex.finny.data.repository

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.stepalex.finny.domain.model.Goal
import com.stepalex.finny.domain.model.Profile
import com.stepalex.finny.domain.repository.ProfileRepository
import kotlinx.serialization.json.Json

class ProfileRepositoryImpl(
    private val json: Json,
    private val prefs: SharedPreferences,
) : ProfileRepository {
    private val keyPrefProfile = "pref_profile"
    private val keyPrefFirstLaunch = "pref_first_lauch"
    private val keyPrefGoals = "pref_goals"

    override suspend fun getProfile(): Result<Profile?> = runCatching {
        val profileString = prefs.getString(keyPrefProfile, null)
        if (profileString.isNullOrEmpty()) {
            return@runCatching null
        }
        return@runCatching try {
            json.decodeFromString<Profile>(profileString)
        } catch (e: Exception) {
            Log.d("TEST", "error = $e")
            null
        }
    }

    override suspend fun updateProfile(profile: Profile): Result<Unit> = runCatching {
        val profileString = json.encodeToString(profile)
        prefs.edit { putString(keyPrefProfile, profileString) }
    }

    override suspend fun getGoals(): Result<List<Goal>> = runCatching {
        val jsonString = prefs.getString(keyPrefGoals, null)
        if (jsonString.isNullOrEmpty()) return@runCatching emptyList()
        return@runCatching try {
            json.decodeFromString<List<Goal>>(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun saveGoals(goals: List<Goal>): Result<Unit> = runCatching {
        val jsonString = json.encodeToString(goals)
        prefs.edit { putString(keyPrefGoals, jsonString) }
    }

    override suspend fun isFirstLaunch(): Boolean {
        return prefs.getBoolean(keyPrefFirstLaunch, true)
    }

    override suspend fun setNotFirstLaunch() {
        prefs.edit { putBoolean(keyPrefFirstLaunch, false) }
    }
}
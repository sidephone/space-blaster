package com.sidephone.spaceblaster.settings

import android.content.SharedPreferences
import androidx.core.content.edit

class Settings(context: android.content.Context) {
	companion object {
		private const val PREFS_NAME = "SpaceBlasterSettings"
		private const val ASTEROIDS_BUMP_KEY = "asteroids_bump"
		private const val BULLETS_WRAP_AROUND_KEY = "bullets_wrap_around"
		private const val HIGH_SCORE_KEY = "high_score"
	}

	object Gameplay {
		const val TARGET_FPS = 120
		const val TARGET_IPS = 60
	}


	private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)


	fun getAsteroidsBump(): Boolean {
		return sharedPreferences.getBoolean(ASTEROIDS_BUMP_KEY, true)
	}

	fun getBulletsWrapAround(): Boolean {
		return sharedPreferences.getBoolean(BULLETS_WRAP_AROUND_KEY, true)
	}

	fun getHighScore(): Int {
		return sharedPreferences.getInt(HIGH_SCORE_KEY, 0)
	}

	fun updateHighScoreIfNeeded(newScore: Int): Boolean {
		if (newScore > sharedPreferences.getInt(HIGH_SCORE_KEY, Int.MIN_VALUE)) {
			sharedPreferences.edit { putInt(HIGH_SCORE_KEY, newScore) }
			return true
		}

		return false
	}
}

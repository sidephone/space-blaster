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

	object Asteroids {
		const val MIN = 3
		const val MAX = 15
		const val NEW_EVERY_N_STAGES = 4 // add an extra asteroid every 4 stages
	}

	object Gameplay {
		const val STAGE_COUNTDOWN = 3000L // ms
		const val TARGET_FPS = 120
		const val TARGET_IPS = 60
	}

	object Player {
		const val BONUS_LIVE_POINTS = 400
		const val INVINCIBILITY_DURATION = 2000L // ms
		const val RESPAWN_DELAY = 1500L // ms
		const val STARTING_LIVES = 3
	}

	object PlayerBullets {
		const val BONUS_EVERY_N_STAGES = 4 // award an extra bullet every 4 stages
		const val BONUS_SHOOT_DELAY_PER_STAGE = 25L // ms (reduce the shoot delay by 25ms every stage)
		const val MIN = 4
		const val MAX = 10
		const val SHOOT_DELAY = 750L // ms
		const val SHOOT_DELAY_MIN = 100L // ms
	}

	object Saucer {
		const val BULLETS_MAX = 10
		const val SHOOT_DELAY_MIN = 1000L // ms
		const val SHOOT_DELAY_MAX = 1500L // ms

		const val FLY_TIME_MIN = 400L // ms
		const val FLY_TIME_MAX = 1650L // ms
		const val STILL_TIME_MIN = 100L
		const val STILL_TIME_MAX = 500L

		const val SPAWN_STAGE_TIME_MIN = 30000L // ms
		const val SPAWN_MIN_STAGE = -1 // only spawn saucers after this stage
		const val SPAWN_WHEN_MAX_ASTEROIDS = 5 // only spawn saucers if there are less than this many asteroids on screen
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
		val currentHighScore = getHighScore()
		if (newScore > currentHighScore) {
			sharedPreferences.edit { putInt(HIGH_SCORE_KEY, newScore) }
			return true
		}

		return false
	}
}

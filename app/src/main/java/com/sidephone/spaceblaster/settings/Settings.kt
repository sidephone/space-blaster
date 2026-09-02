package com.sidephone.spaceblaster.settings

import android.content.SharedPreferences

class Settings(context: android.content.Context) {
	companion object {
		private const val PREFS_NAME = "SnakeSettings"
		private const val ASTEROIDS_BUMP_KEY = "asteroids_bump"
	}

	object Gameplay {
		const val TARGET_FPS = 120
		const val TARGET_IPS = 60
	}


	private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)


	fun getAsteroidsBump(): Boolean {
		return sharedPreferences.getBoolean(ASTEROIDS_BUMP_KEY, true)
	}
}

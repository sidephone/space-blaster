package com.sidephone.spaceblaster.engine.entities

import com.sidephone.spaceblaster.engine.graphics.DrawCommandGroup
import com.sidephone.spaceblaster.settings.Settings

class AsteroidList {
	companion object {
		const val MIN_ASTEROIDS = 3
	}

	private val asteroids: MutableList<Asteroid> = mutableListOf()

	private var bumpAsteroids = false


	fun spawn(
		settings: Settings?,
		stage: Int,
		player: Ship,
		viewportWidth: Float,
		viewportHeight: Float
	) {
		bumpAsteroids = settings?.getAsteroidsBump() == true

		asteroids.clear()
		val minDistanceToPlayer = player.radius() * 3f

		for (i in 1 until MIN_ASTEROIDS + stage) {
			asteroids.add(Asteroid().spawn(
				Asteroid.SIZE.entries.random(),
				player.position(),
				minDistanceToPlayer,
				viewportWidth,
				viewportHeight
			))
		}
	}


	fun move(now: Long, viewportWidth: Float, viewportHeight: Float) {
		for ((i, asteroid) in asteroids.withIndex()) {
			if (bumpAsteroids) {
				for (otherIndex in i + 1 until asteroids.size) {
					if (asteroid.shouldBump(asteroids[otherIndex])) {
						asteroid.bump(asteroids[otherIndex])
					}
				}
			}

			asteroid.move(now, viewportWidth, viewportHeight)
		}
	}


	fun draw(now: Long): List<DrawCommandGroup> {
		return asteroids.map { asteroid -> asteroid.draw(now) }
	}
}

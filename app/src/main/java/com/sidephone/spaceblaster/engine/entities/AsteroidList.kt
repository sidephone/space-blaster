package com.sidephone.spaceblaster.engine.entities

import com.sidephone.spaceblaster.engine.graphics.DrawCommandGroup
import com.sidephone.spaceblaster.settings.Settings

class AsteroidList {
	companion object {
		const val MIN_ASTEROIDS = 3
	}

	private val asteroids: MutableList<Asteroid> = mutableListOf()

	private var bumpAsteroids = false
	private var bumpsWithPlayer: Int = -1


	fun oneBumpsWithPlayer(): Int {
		return bumpsWithPlayer
	}


	fun spawn(
		settings: Settings?,
		stage: Int,
		player: Ship,
		viewportWidth: Float,
		viewportHeight: Float
	) {
		bumpAsteroids = settings?.getAsteroidsBump() == true

		asteroids.clear()

		for (i in 1 until MIN_ASTEROIDS + stage) {
			asteroids.add(Asteroid().spawn(
				Asteroid.SIZE.LARGE,
				null,
				null,
				player.position(),
				player.minAsteroidSpawnDistance(),
				viewportWidth,
				viewportHeight
			))
		}
	}


	fun move(now: Long, player: Ship, viewportWidth: Float, viewportHeight: Float) {
		bumpsWithPlayer = -1

		for ((i, asteroid) in asteroids.withIndex()) {
			if (asteroid.shouldBump(player)) {
				bumpsWithPlayer = i
			} else if (bumpAsteroids) {
				for (otherIndex in i + 1 until asteroids.size) {
					if (asteroid.shouldBump(asteroids[otherIndex])) {
						asteroid.bump(asteroids[otherIndex])
					}
				}
			}

			asteroid.move(now, viewportWidth, viewportHeight)
		}
	}


	fun split(index: Int, player: Ship, viewportWidth: Float, viewportHeight: Float) {
		val asteroid = asteroids.removeAt(index)

		val newType = if (asteroid.isLarge()) {
			Asteroid.SIZE.MEDIUM
		} else if (asteroid.isMedium()) {
			Asteroid.SIZE.SMALL
		} else {
			return
		}

		for (i in 1..2) {
			val newDirection = player.speedDirection() + (15f - 30f * Math.random().toFloat())

			asteroids.add(Asteroid().spawn(
				newType,
				asteroid.position(),
				newDirection,
				player.position(),
				player.minAsteroidSpawnDistance(),
				viewportWidth,
				viewportHeight
			))
		}
	}


	fun draw(now: Long): List<DrawCommandGroup> {
		return asteroids.map { asteroid -> asteroid.draw(now) }
	}
}

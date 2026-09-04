package com.sidephone.spaceblaster.engine.entities.asteroids

import com.sidephone.spaceblaster.engine.entities.ships.Ship
import com.sidephone.spaceblaster.engine.graphics.DrawCommandGroup
import com.sidephone.spaceblaster.settings.Settings

class AsteroidList {
	companion object {
		const val MIN_ASTEROIDS = 3
	}

	private val asteroids: MutableList<Asteroid> = mutableListOf()

	private var bumpAsteroids = false
	private var bumpsWithPlayer: Int = -1


	fun draw(now: Long): List<DrawCommandGroup> {
		return asteroids.map { asteroid -> asteroid.draw(now) }
	}


	fun move(now: Long, player: Ship, viewportWidth: Float, viewportHeight: Float) {
		bumpsWithPlayer = -1

		for ((i, asteroid) in asteroids.withIndex()) {
			if (asteroid.shouldBump(now, player)) {
				bumpsWithPlayer = i
			} else if (bumpAsteroids) {
				for (otherIndex in i + 1 until asteroids.size) {
					if (asteroid.shouldBump(now, asteroids[otherIndex])) {
						asteroid.bump(asteroids[otherIndex])
					}
				}
			}

			asteroid.move(now, viewportWidth, viewportHeight)
		}
	}


	fun oneBumpsWithPlayer(): Int {
		return bumpsWithPlayer
	}


	fun position(index: Int): Pair<Float, Float>? {
		if (index < 0 || index >= asteroids.size) {
			return null
		}

		return asteroids[index].position()
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

		repeat(MIN_ASTEROIDS + stage - 1) {
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


	fun split(index: Int, player: Ship, viewportWidth: Float, viewportHeight: Float) {
		val asteroid = asteroids.removeAt(index)

		val newType = if (asteroid.isLarge()) {
			Asteroid.SIZE.MEDIUM
		} else if (asteroid.isMedium()) {
			Asteroid.SIZE.SMALL
		} else {
			return
		}

		repeat(2) {
			val newDirection = player.speedDirection() + (30f - 60f * Math.random().toFloat())

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
}

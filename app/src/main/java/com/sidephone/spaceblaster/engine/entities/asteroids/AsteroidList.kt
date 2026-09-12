package com.sidephone.spaceblaster.engine.entities.asteroids

import com.sidephone.spaceblaster.engine.entities.ships.Ship
import com.sidephone.spaceblaster.settings.Settings.Asteroids.MAX
import com.sidephone.spaceblaster.settings.Settings.Asteroids.MIN
import com.sidephone.spaceblaster.settings.Settings.Asteroids.NEW_EVERY_N_STAGES

class AsteroidList {
	private val asteroids: MutableList<Asteroid> = mutableListOf()

	private var bumpAsteroids = false


	fun clear() { asteroids.clear() }
	fun count() = asteroids.size
	fun draw() = asteroids.map { asteroid -> asteroid.draw() }
	fun isEmpty() = asteroids.isEmpty()
	fun getAll() = asteroids.toList()
	fun position(index: Int) = asteroids.getOrNull(index)?.position()
	fun score(index: Int): Int = asteroids.getOrNull(index)?.score() ?: 0


	fun move(now: Long, viewportWidth: Float, viewportHeight: Float) {
		for ((i, asteroid) in asteroids.withIndex()) {
			if (bumpAsteroids) {
				for (otherIndex in i + 1 until asteroids.size) {
					if (asteroid.shouldBump(now, asteroids[otherIndex])) {
						asteroid.bump(asteroids[otherIndex])
					}
				}
			}

			asteroid.move(now, viewportWidth, viewportHeight)
		}
	}


	fun oneCrashesWith(now: Long, ship: Ship): Int {
		for ((i, asteroid) in asteroids.withIndex()) {
			if (asteroid.shouldBump(now, ship)) {
				return i
			}
		}

		return -1
	}


	fun spawn(areAsteroidsBumpable: Boolean, stage: Int, viewportWidth: Float, viewportHeight: Float) {
		bumpAsteroids = areAsteroidsBumpable

		asteroids.clear()

		val numAsteroids = (MIN + (stage / NEW_EVERY_N_STAGES)).coerceAtMost(MAX)
		repeat(numAsteroids) {
			asteroids.add(Asteroid().spawn(
				Asteroid.SIZE.LARGE,
				viewportWidth,
				viewportHeight
			))
		}
	}


	fun split(index: Int, blastDirection: Float, randomizeSpeed: Boolean, viewportWidth: Float, viewportHeight: Float) {
		val asteroid = asteroids.removeAt(index)

		val newType = if (asteroid.isLarge()) {
			Asteroid.SIZE.MEDIUM
		} else if (asteroid.isMedium()) {
			Asteroid.SIZE.SMALL
		} else {
			return
		}

		repeat(2) {
			asteroids.add(Asteroid().spawn(
				newType,
				asteroid.position(),
				blastDirection + (45f - 90f * Math.random().toFloat()),
				randomizeSpeed,
				viewportWidth,
				viewportHeight
			))
		}
	}
}

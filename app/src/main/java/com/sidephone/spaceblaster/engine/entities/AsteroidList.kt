package com.sidephone.spaceblaster.engine.entities

import com.sidephone.spaceblaster.engine.graphics.DrawCommandGroup

class AsteroidList : Iterable<Asteroid> {
	companion object {
		const val MIN_ASTEROIDS = 3
	}

	private val asteroids: MutableList<Asteroid> = mutableListOf()

	override fun iterator(): Iterator<Asteroid> = asteroids.iterator()


	fun spawn(stage: Int, player: Ship, viewportWidth: Float, viewportHeight: Float) {
		asteroids.clear()
		val minDistanceToPlayer = player.radius() * 3f

		for (i in 1 until MIN_ASTEROIDS + stage) {
			asteroids.add(Asteroid().spawn(
				Asteroid.SIZE.LARGE,
				player.position(),
				minDistanceToPlayer,
				viewportWidth,
				viewportHeight
			))
		}
	}


	fun move(now: Long, viewportWidth: Float, viewportHeight: Float) {
		for (asteroid in asteroids) {
			asteroid.move(now, viewportWidth, viewportHeight)
		}
	}


	fun draw(now: Long): List<DrawCommandGroup> {
		return asteroids.map { asteroid -> asteroid.draw(now) }
	}
}

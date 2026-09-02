package com.sidephone.spaceblaster.engine.entities

import android.util.Log
import com.sidephone.spaceblaster.engine.entities.asteroids.AsteroidType
import com.sidephone.spaceblaster.engine.entities.asteroids.LargeAsteroid
import com.sidephone.spaceblaster.engine.entities.asteroids.MediumAsteroid
import com.sidephone.spaceblaster.engine.entities.asteroids.SmallAsteroid
import com.sidephone.spaceblaster.engine.graphics.DrawCommandGroup
import com.sidephone.spaceblaster.settings.Settings
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Asteroid {
	companion object {
		const val MIN_AMOUNT = 4
	}

	enum class SIZE {
		LARGE, MEDIUM, SMALL
	}

	private var asteroidType: AsteroidType = LargeAsteroid()

	private var direction: Float = 0f // degrees, 0 is to the right, -90 is straight up
	private var x: Float = 0f // px, center of the ship
	private var y: Float = 0f // px, center of the ship
	private var speedX = 0f
	private var speedY = 0f

	private var moveDtMax: Float = 1f
	private var turnStepMax: Float = 1f
	private var lastMoveTime = 0L // ms


	/**
	 * Spawn a random asteroid of a given size, not too close to the player
	 */
	fun spawn(size: SIZE, playerPosition: Pair<Float, Float>, minDistanceToPlayer: Float, viewportWidth: Float, viewportHeight: Float): Asteroid {
		asteroidType = when (size) {
			SIZE.LARGE -> LargeAsteroid()
			SIZE.MEDIUM -> MediumAsteroid()
			SIZE.SMALL -> SmallAsteroid()
		}

		var distanceToPlayer: Float
		do {
			x = (viewportWidth * Math.random()).toFloat()
			y = (viewportHeight * Math.random()).toFloat()
			distanceToPlayer = sqrt((x - playerPosition.first) * (x - playerPosition.first) + (y - playerPosition.second) * (y - playerPosition.second))
		} while (distanceToPlayer < minDistanceToPlayer)


		direction = 360f * Math.random().toFloat()
		speedX = asteroidType.speed() * cos(Math.toRadians(direction.toDouble())).toFloat()
		speedY = asteroidType.speed() * sin(Math.toRadians(direction.toDouble())).toFloat()

		moveDtMax = 10f / Settings.Gameplay.TARGET_IPS.toFloat()
		turnStepMax = asteroidType.turnSpeed() / Settings.Gameplay.TARGET_IPS.toFloat()
		lastMoveTime = 0L

		return this
	}


	fun move(now: Long, viewportWidth: Float, viewportHeight: Float) {
		val dt = ((now - lastMoveTime) / 1000f).coerceAtMost(moveDtMax)
		var turnSpeed = (asteroidType.turnSpeed() * (now - lastMoveTime) / 1000f)
		turnSpeed = turnSpeed.coerceAtMost(turnStepMax).coerceAtLeast(-turnStepMax)

		val oldX = x
		val oldY = y

		direction += turnSpeed
		x += speedX * dt
		y += speedY * dt


		// wrap around the screen edges
		if (x < 0) x = viewportWidth
		if (y < 0) y = viewportHeight
		if (x > viewportWidth) x = 0f
		if (y > viewportHeight) y = 0f

		lastMoveTime = now
	}


	fun draw(now: Long): DrawCommandGroup {
		return DrawCommandGroup(x, y, direction, asteroidType.draw())
	}
}

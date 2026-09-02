package com.sidephone.spaceblaster.engine.entities

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


	fun radius(): Float = asteroidType.radius()
	fun mass(): Float = asteroidType.mass()


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


	/**
	 * Checks whether this asteroid and `other` are close enough to collide
	 * (distance between centers <= sum of radii) AND are currently
	 * approaching each other (rather than already moving apart).
	 */
	fun shouldBump(other: Asteroid): Boolean {
		val dx = other.x - x
		val dy = other.y - y
		val distance = sqrt(dx * dx + dy * dy)
		val combinedRadii = radius() + other.radius()

		if (distance > combinedRadii) {
			return false
		}

		// Approaching if the relative velocity, projected onto the line
		// connecting the two centers, points from us toward the other
		// (i.e. the distance between them is decreasing).
		val relativeVelocityX = speedX - other.speedX
		val relativeVelocityY = speedY - other.speedY
		val closingSpeed = relativeVelocityX * dx + relativeVelocityY * dy

		return closingSpeed > 0f
	}

	/**
	 * Re-calculate the speed of this asteroid and `other` when they collide, assuming a perfectly
	 * elastic collision. Positions are not changed, move() will handle that in the next frame by
	 * using the new speedX and speedY values.
	 */
	fun bump(other: Asteroid) {
		val dx = other.x - x
		val dy = other.y - y
		val distance = sqrt(dx * dx + dy * dy)

		if (distance == 0f) {
			return // avoid division by zero for exactly-overlapping centers
		}

		// unit normal along the line connecting the two centers
		val nx = dx / distance
		val ny = dy / distance

		val relativeSpeedX = speedX - other.speedX
		val relativeSpeedY = speedY - other.speedY
		val speedAlongNormal = relativeSpeedX * nx + relativeSpeedY * ny

		if (speedAlongNormal <= 0f) {
			return // already flying apart, nothing to do
		}

		// elastic collision impulse magnitude along the normal
		val impulse = (2f * speedAlongNormal) / (mass() + other.mass())

		speedX -= impulse * other.mass() * nx
		speedY -= impulse * other.mass() * ny
		other.speedX += impulse * mass() * nx
		other.speedY += impulse * mass() * ny
	}


	fun move(now: Long, viewportWidth: Float, viewportHeight: Float) {
		val dt = ((now - lastMoveTime) / 1000f).coerceAtMost(moveDtMax)
		var turnSpeed = (asteroidType.turnSpeed() * (now - lastMoveTime) / 1000f)
		turnSpeed = turnSpeed.coerceAtMost(turnStepMax).coerceAtLeast(-turnStepMax)

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

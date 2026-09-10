package com.sidephone.spaceblaster.engine.entities.asteroids

import com.sidephone.spaceblaster.engine.entities.SpaceObject
import com.sidephone.spaceblaster.engine.graphics.DrawCommandGroup
import com.sidephone.spaceblaster.settings.Settings
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Asteroid : SpaceObject {
	enum class SIZE {
		LARGE, MEDIUM, SMALL
	}

	private var asteroidType: AsteroidType = AsteroidTypeLarge()

	private var direction: Float = 0f // degrees, 0 is to the right, -90 is straight up
	private var x: Float = 0f // px, center of the ship
	private var y: Float = 0f // px, center of the ship
	private var speedX = 0f
	private var speedY = 0f

	private var moveDtMax: Float = 1f
	private var turnStepMax: Float = 1f
	private var lastMoveTime = 0L // ms


	override fun notBumpable(now: Long): Boolean = false
	override fun position(): Pair<Float, Float> = Pair(x, y)
	override fun radius(): Float = asteroidType.radius()
	override fun speed(): Pair<Float, Float> = Pair(speedX, speedY)

	fun isLarge(): Boolean = asteroidType is AsteroidTypeLarge
	fun isMedium(): Boolean = asteroidType is AsteroidTypeMedium
	fun mass(): Float = asteroidType.mass()
	fun score(): Int = asteroidType.score()


	/**
	 * Spawn a random asteroid of a given size, not too close to the player
	 */
	fun spawn(size: SIZE, playerPosition: Pair<Float, Float>, minDistanceToPlayer: Float, viewportWidth: Float, viewportHeight: Float): Asteroid {
		return spawn(size, null, null, true, playerPosition, minDistanceToPlayer, viewportWidth, viewportHeight)
	}


	/**
	 * Spawn a random asteroid of a given size at a given position and direction, with optional random speed
	 */
	fun spawn(size: SIZE, spawnPosition: Pair<Float, Float>?, direction: Float?, randomSpeed: Boolean, playerPosition: Pair<Float, Float>, minDistanceToPlayer: Float, viewportWidth: Float, viewportHeight: Float): Asteroid {
		asteroidType = when (size) {
			SIZE.LARGE -> AsteroidTypeLarge()
			SIZE.MEDIUM -> AsteroidTypeMedium()
			SIZE.SMALL -> AsteroidTypeSmall()
		}

		// if no position is provided, spawn the asteroid at a random position outside the viewport, but
		// not too close to the player
		if (spawnPosition == null) {
			var distanceToPlayer: Float
			repeat(5) {
				x = AsteroidTypeLarge.RADIUS * (1 + Math.random().toFloat())
				if (Math.random() < 0.5) {
					x += viewportWidth
					this.direction = 135f + 90f * Math.random().toFloat()
				} else {
					x = -x
					this.direction = 315f + 90f * Math.random().toFloat()
				}

				y = viewportHeight * Math.random().toFloat()
				distanceToPlayer = sqrt((x - playerPosition.first) * (x - playerPosition.first) + (y - playerPosition.second) * (y - playerPosition.second))
				if (distanceToPlayer >= minDistanceToPlayer) return@repeat
			}
		}
		// if a position is provided (e.g. when splitting an asteroid), use that position
		else {
			x = spawnPosition.first
			y = spawnPosition.second
		}

		if (direction != null) {
			this.direction = direction
		}

		val speedXRatio = if (randomSpeed) 0.15f + Math.random().toFloat() * 0.85f else 1f
		val speedYRatio = if (randomSpeed) 0.15f + Math.random().toFloat() * 0.85f else 1f

		speedX = speedXRatio * asteroidType.speed() * cos(Math.toRadians(this.direction.toDouble())).toFloat()
		speedY = speedYRatio * asteroidType.speed() * sin(Math.toRadians(this.direction.toDouble())).toFloat()

		moveDtMax = 10f / Settings.Gameplay.TARGET_IPS.toFloat()
		turnStepMax = asteroidType.turnSpeed() / Settings.Gameplay.TARGET_IPS.toFloat()
		lastMoveTime = 0L

		return this
	}


	/**
	 * Checks whether this asteroid and the other object are close enough to collide(distance between
	 * centers <= sum of radii) AND are currently approaching each other
	 */
	fun shouldBump(now: Long, other: SpaceObject): Boolean {
		if (other.notBumpable(now)) return false

		val dx = other.position().first - x
		val dy = other.position().second - y
		val centerDistance = sqrt(dx * dx + dy * dy)
		val surfaceDistance = radius() + other.radius()

		if (centerDistance > surfaceDistance) {
			return false
		}

		// Approaching if the relative velocity, projected onto the line
		// connecting the two centers, points from us toward the other
		// (i.e. the distance between them is decreasing).
		val relativeSpeedX = speedX - other.speed().first
		val relativeSpeedY = speedY - other.speed().second
		val closingSpeed = relativeSpeedX * dx + relativeSpeedY * dy

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
		val rawTurnSpeed = asteroidType.turnSpeed() * (now - lastMoveTime) / 1000f
		val maxTurnStep = if (turnStepMax < 0f) -turnStepMax else turnStepMax
		val turnSpeed = rawTurnSpeed.coerceIn(-maxTurnStep, maxTurnStep)

		direction += turnSpeed
		val dx = speedX * dt
		val dy = speedY * dt

		x += dx
		y += dy

		// wrap around the screen edges
		if (x < 0 && dx < 0) x = viewportWidth
		if (y < 0 && dy < 0) y = viewportHeight
		if (x > viewportWidth && dx > 0) x = 0f
		if (y > viewportHeight && dy > 0) y = 0f

		lastMoveTime = now
	}


	fun draw(): DrawCommandGroup {
		return DrawCommandGroup(x, y, asteroidType.draw(), direction)
	}
}

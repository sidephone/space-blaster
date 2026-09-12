package com.sidephone.spaceblaster.engine.entities.ships

import com.sidephone.spaceblaster.engine.entities.asteroids.Asteroid
import com.sidephone.spaceblaster.engine.entities.getEnemySpawnPosition
import com.sidephone.spaceblaster.engine.graphics.DrawCommandGroup
import com.sidephone.spaceblaster.settings.Settings.Saucer.AVOID_ASTEROID_CHANCE
import com.sidephone.spaceblaster.settings.Settings.Saucer.AVOID_ASTEROID_DISTANCE
import com.sidephone.spaceblaster.settings.Settings.Saucer.AVOID_ASTEROID_RETRIES
import com.sidephone.spaceblaster.settings.Settings.Saucer.FLY_TIME_MAX
import com.sidephone.spaceblaster.settings.Settings.Saucer.FLY_TIME_MIN
import com.sidephone.spaceblaster.settings.Settings.Saucer.SPAWN_MIN_STAGE
import com.sidephone.spaceblaster.settings.Settings.Saucer.SPAWN_STAGE_TIME_MIN
import com.sidephone.spaceblaster.settings.Settings.Saucer.SPAWN_WHEN_MAX_ASTEROIDS
import com.sidephone.spaceblaster.settings.Settings.Saucer.SPAWN_WHEN_MIN_ASTEROIDS
import com.sidephone.spaceblaster.settings.Settings.Saucer.STILL_TIME_MAX
import com.sidephone.spaceblaster.settings.Settings.Saucer.STILL_TIME_MIN
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt


class EnemyShip : Ship() {
	private var avoidAsteroidsRadius = 0f
	private var isDead = true
	private var nextDirectionChange = 0L
	private var lastStage = 0
	private var score = 0


	override fun isDead(now: Long) = isDead
	fun score() = score


	/**
	 * All objects are circles, so we aim at the diameter that is perpendicular to the line from our
	 * center to the target's center. If we hit that diameter, we have hit the target. To account for
	 * our (in)accuracy, we expand the diameter by a percentage of the viewport size. The lower our
	 * accuracy, the larger the diameter.
	 */
	fun aim(viewportWidth: Float, viewportHeight: Float, target: Pair<Float, Float>, targetRadius: Float) {
		if (isDead) return

		val accuracy = when (shipType) {
			is ShipTypeSaucerSmall -> ShipTypeSaucerSmall.AIM_ACCURACY
			is ShipTypeSaucerBig -> ShipTypeSaucerBig.AIM_ACCURACY
			else -> 0f
		}

		// expand the target diameter by a percentage of the viewport size based on our accuracy
		val assumedDiameter = targetRadius * 2 + (1 - accuracy) * max(viewportWidth, viewportHeight)
		val assumedRadius = assumedDiameter / 2

		// calculate the coordinates of the diameter endpoints
		val dx = target.first - x
		val dy = target.second - y
		val targetCenterDistance = sqrt((dx * dx + dy * dy).toDouble())
		val perpDx = -dy / targetCenterDistance * assumedRadius
		val perpDy = dx / targetCenterDistance * assumedRadius
		val endpoint1X = target.first + perpDx
		val endpoint1Y = target.second + perpDy
		val endpoint2X = target.first - perpDx
		val endpoint2Y = target.second - perpDy

		// aim at a random point between the two endpoints
		val randomFactor = Math.random().toFloat()
		val aimX = endpoint1X + randomFactor * (endpoint2X - endpoint1X)
		val aimY = endpoint1Y + randomFactor * (endpoint2Y - endpoint1Y)

		// get the angle to the aim point
		direction = Math.toDegrees(atan2((aimY - y), (aimX - x))).toFloat()
	}


	private fun calculateNextMove(now: Long, runAway: Boolean) {
		if (now < nextDirectionChange && !runAway) {
			return
		}

		isThrusting = !isThrusting || runAway

		if (isThrusting) {
			calculateSpeed(2 * Math.PI * Math.random())
			nextDirectionChange = now + (FLY_TIME_MIN..FLY_TIME_MAX).random()
		} else {
			speedX = 0f
			speedY = 0f
			nextDirectionChange = now + (STILL_TIME_MIN..STILL_TIME_MAX).random()
		}
	}


	private fun calculateSpeed(angle: Double) {
		speedX = (shipType.maxSpeed() * cos(angle)).toFloat()
		speedY = (shipType.maxSpeed() * sin(angle)).toFloat()
	}


	override fun die(now: Long) {
		isDead = true

		// prevent collisions and hits by bullets
		x = Float.MIN_VALUE
		y = Float.MIN_VALUE
	}


	override fun draw(now: Long): DrawCommandGroup {
		return if (isDead) {
			DrawCommandGroup(0f, 0f,emptyList())
		} else {
			DrawCommandGroup(x, y, shipType.draw(now, isThrusting))
		}
	}


	private fun isDangerouslyApproachingAsteroid(oldX: Float, oldY: Float, asteroids: List<Asteroid>): Boolean {
		for (asteroid in asteroids) {
			val dx = oldX - asteroid.position().first
			val dy = oldY - asteroid.position().second
			val oldDistance = sqrt(dx * dx + dy * dy)

			val ndx = x - asteroid.position().first
			val ndy = y - asteroid.position().second
			val newDistance = sqrt(ndx * ndx + ndy * ndy)

			if (newDistance < avoidAsteroidsRadius + asteroid.radius() && newDistance < oldDistance) {
				return true
			}
		}
		return false
	}


	fun isOnScreen(viewportWidth: Float, viewportHeight: Float): Boolean {
		return x >= -shipType.radius()
			&& x <= viewportWidth + shipType.radius()
			&& y >= -shipType.radius()
			&& y <= viewportHeight + shipType.radius()
	}


	fun isTimeToSpawn(stage: Int, stageTime: Long, asteroidCount: Int): Boolean {
		return asteroidCount in SPAWN_WHEN_MIN_ASTEROIDS..SPAWN_WHEN_MAX_ASTEROIDS
			&& isDead
			&& lastStage != stage
			&& stage >= SPAWN_MIN_STAGE
			&& stageTime >= SPAWN_STAGE_TIME_MIN
	}


	fun moveAtWill(now: Long, asteroids: List<Asteroid>, viewportWidth: Float, viewportHeight: Float) {
		if (isDead) return

		val oldX = x
		val oldY = y
		val previousLastMoveTime = lastMoveTime
		val avoidAsteroids = Math.random() < AVOID_ASTEROID_CHANCE
		var retries = if (avoidAsteroids) AVOID_ASTEROID_RETRIES else 1
		var runAway = false

		while (retries-- > 0) {
			calculateNextMove(now, runAway)
			move(now, viewportWidth, viewportHeight)
			if (isDangerouslyApproachingAsteroid(oldX, oldY, asteroids)) {
				nextDirectionChange = now
				x = oldX
				y = oldY
				lastMoveTime = previousLastMoveTime
				runAway = true
			} else {
				break
			}
		}
	}


	fun reset() {
		lastStage = 0
		die(0)
	}


	override fun spawn(now: Long, viewportWidth: Float, viewportHeight: Float) {
		super.spawn(now, viewportWidth, viewportHeight)
		shipType = if ((0..1).random() == 0) ShipTypeSaucerBig() else ShipTypeSaucerSmall()

		getEnemySpawnPosition(viewportWidth, viewportHeight, shipType.radius()).let { (spawnX, spawnY, spawnDirection) ->
			x = spawnX
			y = spawnY
			calculateSpeed(Math.toRadians(spawnDirection.toDouble()))
		}

		avoidAsteroidsRadius = shipType.radius() * AVOID_ASTEROID_DISTANCE
		isDead = false
		isThrusting = true
		nextDirectionChange = now + FLY_TIME_MIN
		score = when (shipType) {
			is ShipTypeSaucerSmall -> ShipTypeSaucerSmall.SCORE_POINTS
			is ShipTypeSaucerBig -> ShipTypeSaucerBig.SCORE_POINTS
			else -> 0
		}
	}


	fun spawnIfNeeded(now: Long, stage: Int, stageTime: Long, asteroidCount: Int, viewportWidth: Float, viewportHeight: Float): Boolean {
		if (isTimeToSpawn(stage, stageTime, asteroidCount)) {
			spawn(now, viewportWidth, viewportHeight)
			lastStage = stage
			return true
		}
		return false
	}
}

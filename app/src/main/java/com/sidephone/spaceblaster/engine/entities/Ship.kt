package com.sidephone.spaceblaster.engine.entities

import com.sidephone.spaceblaster.engine.entities.ships.DefenderShip
import com.sidephone.spaceblaster.engine.entities.ships.ShipType
import com.sidephone.spaceblaster.engine.graphics.DrawCommandGroup
import com.sidephone.spaceblaster.settings.Settings
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class Ship : SpaceObject {
	companion object {
		const val INVINCIBILITY_DURATION = 2000L // ms
		const val RESPAWN_DELAY = 1500L // ms
		const val STARTING_LIVES = 3
	}

	private var shipType: ShipType = DefenderShip()

	private var direction: Float = 0f // degrees, 0 is to the right, -90 is straight up
	private var x: Float = 0f // px, center of the ship
	private var y: Float = 0f // px, center of the ship
	private var speedX = 0f
	private var speedY = 0f
	private var accelerationMax: Float = 1f
	private var brakingMax: Float = 1f
	private var moveDtMax: Float = 1f
	private var turnStepMax: Float = 1f

	private var lastDeathTime = 0L
	private var lastThrustTime = 0L // ms
	private var lastMoveTime = 0L // ms
	private var lastTurnTime = 0L // ms

	private var isThrusting = false
	private var isInvincible = false
	private var invincibilityTimeout = 0L // ms

	private var lives = STARTING_LIVES


	override fun notBumpable(now: Long): Boolean = isDead(now) || isInvincible
	override fun position(): Pair<Float, Float> = Pair(x, y)
	override fun radius(): Float = shipType.radius()
	override fun speed(): Pair<Float, Float> = Pair(speedX, speedY)

	fun isDead(now: Long) = lives <= 0 || (lastDeathTime + RESPAWN_DELAY > now)
	fun minAsteroidSpawnDistance(): Float = shipType.radius() * 3f
	fun speedDirection(): Float = Math.toDegrees(atan2(speedY.toDouble(), speedX.toDouble())).toFloat()


	fun autoSpawnAfterDeath(now: Long, viewportWidth: Float, viewportHeight: Float) {
		if (lives <= 0 || lastDeathTime == 0L) return

		if (now - lastDeathTime >= RESPAWN_DELAY) {
			spawn(now, viewportWidth, viewportHeight)
		}
	}


	fun die(now: Long) {
		if (isDead(now)) return
		lives--
		lastDeathTime = now
	}


	/**
	 * Use the current ship speed to calculate the new position of the ship based on the elapsed time
	 * since the last move. Movement could occur after calling thrust(), but also when the ship is
	 * coasting in space.
	 */
	fun move(now: Long, viewportWidth: Float, viewportHeight: Float) {
		if (isDead(now)) return

		val dt = ((now - lastMoveTime) / 1000f).coerceAtMost(moveDtMax)
		lastMoveTime = now

		x += speedX * dt
		y += speedY * dt

		// wrap around the screen edges
		if (x < 0) x = viewportWidth
		if (y < 0) y = viewportHeight
		if (x > viewportWidth) x = 0f
		if (y > viewportHeight) y = 0f
	}


	fun revokeInvincibilityWhenExpired(now: Long) {
		if (now >= invincibilityTimeout) {
			isInvincible = false
		}
	}


	fun spawn(now: Long, viewportWidth: Float, viewportHeight: Float) {
		if (isDead(now)) return

		shipType = DefenderShip()

		direction = shipType.drawDirection()
		x = viewportWidth / 2f
		y = viewportHeight / 2f
		speedX = 0f
		speedY = 0f
		accelerationMax = shipType.acceleration() / Settings.Gameplay.TARGET_IPS.toFloat()
		brakingMax = shipType.braking() / Settings.Gameplay.TARGET_IPS.toFloat()
		moveDtMax = 10f / Settings.Gameplay.TARGET_IPS.toFloat()
		turnStepMax = shipType.turnSpeed() / Settings.Gameplay.TARGET_IPS.toFloat()

		lastThrustTime = 0L
		lastMoveTime = 0L
		lastTurnTime = 0L

		isThrusting = false
		isInvincible = true
		invincibilityTimeout = now + INVINCIBILITY_DURATION
		lastDeathTime = 0L
	}


	fun resetLives() {
		lives = STARTING_LIVES
	}


	/**
	 * Calculate the new speed of the ship based on its braking power. This does NOT change the
	 * position of the ship, that is done in move().
	 */
	fun stop(now: Long) {
		if (isDead(now)) return

		isThrusting = false

		val dt = (now - lastThrustTime).coerceAtLeast(0L) / 1000f
		lastThrustTime = now

		val speed = sqrt(speedX * speedX + speedY * speedY)
		if (speed <= 0f || dt <= 0f) return

		val moveSpeed = (shipType.braking() * dt).coerceAtMost(brakingMax)
		val newSpeed = (speed - moveSpeed).coerceAtLeast(0f)

		if (newSpeed == 0f) {
			speedX = 0f
			speedY = 0f
			return
		}

		val scale = newSpeed / speed
		speedX *= scale
		speedY *= scale
	}


	/**
	 * Calculate the new speed of the ship based on its acceleration and direction. This does NOT
	 * change the position of the ship, that is done in move().
	 */
	fun thrust(now: Long, thrusting: Boolean) {
		if (isDead(now)) return

		isThrusting = thrusting
		if (!isThrusting) return

		val dt = (now - lastThrustTime) / 1000f
		lastThrustTime = now

		val angle = Math.toRadians(direction.toDouble())
		val acceleration = shipType.acceleration()
		val moveSpeed = (acceleration * dt).coerceAtMost(accelerationMax)

		speedX += (moveSpeed * cos(angle).toFloat())
		speedY += (moveSpeed * sin(angle).toFloat())

		val speed = sqrt(speedX * speedX + speedY * speedY)
		if (speed > shipType.maxSpeed()) {
			val scale = shipType.maxSpeed() / speed
			speedX *= scale
			speedY *= scale
		}
	}


	/**
	 * Change the ship orientation
	 */
	fun turn(now: Long, left: Boolean) {
		if (isDead(now)) return

		val turnSpeed = (shipType.turnSpeed() * (now - lastTurnTime) / 1000f).coerceAtMost(turnStepMax)
		lastTurnTime = now

		direction += if (left) -turnSpeed else turnSpeed
	}


	fun draw(now: Long): DrawCommandGroup {
		if (isDead(now)) {
			return DrawCommandGroup(0f, 0f, 0f, emptyList())
		}

		val blink = (isInvincible && (now % 200L < 100L))

		return DrawCommandGroup(
			x,
			y,
			direction - shipType.drawDirection(),
			if (blink) emptyList() else shipType.draw(now, isThrusting)
		)
	}
}

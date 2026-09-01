package com.sidephone.spaceblaster.engine.entities

import com.sidephone.spaceblaster.engine.entities.ships.DefenderShip
import com.sidephone.spaceblaster.engine.entities.ships.ShipTypeInterface
import com.sidephone.spaceblaster.engine.graphics.DrawCommand
import com.sidephone.spaceblaster.engine.graphics.DrawCommandGroup
import com.sidephone.spaceblaster.settings.Settings
import kotlin.math.cos
import kotlin.math.sin


class Ship {
	private var shipType: ShipTypeInterface = DefenderShip()
	private var drawCommands: List<DrawCommand> = listOf()

	private var direction: Float = 0f // degrees, 0 is to the right, -90 is straight up
	private var x: Float = 0f // px, center of the ship
	private var y: Float = 0f // px, center of the ship
	private var speedX = 0f
	private var speedY = 0f
	private var accelerationMax: Float = 1f
	private var turnStepMax: Float = 1f

	private var lastThrustTime = 0L // ms
	private var lastMoveTime = 0L // ms
	private var lastTurnTime = 0L // ms

	private var isThrusting = false


	fun spawn(viewportWidth: Float, viewportHeight: Float) {
		shipType = DefenderShip()

		direction = shipType.drawDirection()
		x = viewportWidth / 2f
		y = viewportHeight / 2f
		speedX = 0f
		speedY = 0f
		accelerationMax = shipType.acceleration() / Settings.TARGET_IPS.toFloat()
		turnStepMax = shipType.turnSpeed() / Settings.TARGET_IPS.toFloat()

		isThrusting = false
	}


	/**
	 * Calculate the new speed of the ship based on its acceleration and direction. This does NOT
	 * change the position of the ship, that is done in move().
	 */
	fun thrust(now: Long, thrusting: Boolean) {
		isThrusting = thrusting
		if (!isThrusting) return

		val angle = Math.toRadians(direction.toDouble())
		val acceleration = shipType.acceleration()
		val dt = (now - lastThrustTime) / 1000f
		val moveSpeed = (acceleration * dt).coerceAtMost(accelerationMax)

		speedX += (moveSpeed * cos(angle).toFloat())
		speedY += (moveSpeed * sin(angle).toFloat())
		speedX = speedX.coerceAtLeast(-DefenderShip.MAX_SPEED).coerceAtMost(DefenderShip.MAX_SPEED)
		speedY = speedY.coerceAtLeast(-DefenderShip.MAX_SPEED).coerceAtMost(DefenderShip.MAX_SPEED)

		lastThrustTime = now
	}


	/**
	 * Calculate the new speed of the ship based on its braking power. This does NOT change the
	 * position of the ship, that is done in move().
	 */
	fun stop(now: Long) {
		val dt = (now - lastThrustTime) / 1000f
		val braking = shipType.braking()
		val moveSpeed = (braking * dt).coerceAtMost(accelerationMax)

		if (speedX > 0) {
			speedX -= moveSpeed
			if (speedX < 0) speedX = 0f
		} else if (speedX < 0) {
			speedX += moveSpeed
			if (speedX > 0) speedX = 0f
		}

		if (speedY > 0) {
			speedY -= moveSpeed
			if (speedY < 0) speedY = 0f
		} else if (speedY < 0) {
			speedY += moveSpeed
			if (speedY > 0) speedY = 0f
		}

		lastThrustTime = now
	}


	/**
	 * Change the ship orientation
	 */
	fun turn(now: Long, left: Boolean) {
		val turnSpeed = (shipType.turnSpeed() * (now - lastTurnTime) / 1000f).coerceAtMost(turnStepMax)
		lastTurnTime = now

		direction += if (left) -turnSpeed else turnSpeed
	}


	/**
	 * Use the current ship speed to calculate the new position of the ship based on the elapsed time
	 * since the last move. Movement could occur after calling thrust(), but also when the ship is
	 * coasting in space.
	 */
	fun move(now: Long, viewportWidth: Float, viewportHeight: Float) {
		val dt = (now - lastMoveTime) / 1000f
		lastMoveTime = now

		x += speedX * dt
		y += speedY * dt

		// wrap around the screen edges
		if (x < 0) x = viewportWidth
		if (y < 0) y = viewportHeight
		if (x > viewportWidth) x = 0f
		if (y > viewportHeight) y = 0f
	}


	fun draw(now: Long): DrawCommandGroup {
		return DrawCommandGroup(
			x,
			y,
			direction - shipType.drawDirection(),
			shipType.draw(now, isThrusting)
		)
	}
}

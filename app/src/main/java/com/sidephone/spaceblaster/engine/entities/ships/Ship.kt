package com.sidephone.spaceblaster.engine.entities.ships

import com.sidephone.spaceblaster.engine.entities.SpaceObject
import com.sidephone.spaceblaster.engine.graphics.DrawCommandGroup
import com.sidephone.spaceblaster.settings.Settings
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

abstract class Ship : SpaceObject {

	protected var shipType: ShipType = ShipTypeDefender()

	protected var direction: Float = 0f // degrees, 0 is to the right, -90 is straight up
	protected var x: Float = 0f // px, center of the ship
	protected var y: Float = 0f // px, center of the ship
	private var speedX = 0f
	private var speedY = 0f
	private var accelerationMax: Float = 1f
	private var brakingMax: Float = 1f
	private var moveDtMax: Float = 1f
	private var turnStepMax: Float = 1f

	private var lastThrustTime = 0L // ms
	private var lastMoveTime = 0L // ms
	private var lastTurnTime = 0L // ms

	protected var isThrusting = false


	abstract fun die(now: Long)
	abstract fun draw(now: Long): DrawCommandGroup
	abstract fun isDead(now: Long): Boolean
	abstract fun isDeadForever(): Boolean
	abstract fun resetLives()


	override fun notBumpable(now: Long): Boolean = isDead(now)
	override fun position(): Pair<Float, Float> = Pair(x, y)
	override fun radius(): Float = shipType.radius()
	override fun speed(): Pair<Float, Float> = Pair(speedX, speedY)

	fun cannonPosition(): Pair<Float, Float> {
		val angle = Math.toRadians(direction.toDouble())
		val length = shipType.cannonLength()
		val cannonX = x + (length * cos(angle)).toFloat()
		val cannonY = y + (length * sin(angle)).toFloat()
		return Pair(cannonX, cannonY)
	}
	fun direction() = direction
	fun minAsteroidSpawnDistance(): Float = shipType.radius() * 3f
	fun speedDirection(): Float = Math.toDegrees(atan2(speedY.toDouble(), speedX.toDouble())).toFloat()



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


	open fun spawn(now: Long, viewportWidth: Float, viewportHeight: Float) {
		if (isDeadForever()) return

		shipType = ShipTypeDefender()

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
	}


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


	fun turn(now: Long, left: Boolean) {
		if (isDead(now)) return

		val turnSpeed = (shipType.turnSpeed() * (now - lastTurnTime) / 1000f).coerceAtMost(turnStepMax)
		lastTurnTime = now

		direction += if (left) -turnSpeed else turnSpeed
	}
}

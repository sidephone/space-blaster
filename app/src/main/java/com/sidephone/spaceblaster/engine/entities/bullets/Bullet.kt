package com.sidephone.spaceblaster.engine.entities.bullets

import com.sidephone.spaceblaster.engine.entities.SpaceObject
import com.sidephone.spaceblaster.engine.graphics.DrawCommand
import com.sidephone.spaceblaster.engine.graphics.DrawCommandGroup
import com.sidephone.spaceblaster.settings.Settings
import kotlin.math.cos
import kotlin.math.sin

class Bullet(isEnemy: Boolean, private val canWrap: Boolean) {
	companion object {
		const val COLOR_ENEMY = 0xFFFF4C00.toInt()
		const val COLOR_PLAYER = 0xFF9ADAC5.toInt()
		const val LIFETIME = 1100L // ms
		const val RADIUS = 4f // px
		const val SPEED = 350f // px/s
	}

	private val drawCommands = listOf(DrawCommand.Circle(
		0f,
		0f,
		RADIUS,
		if (isEnemy) COLOR_ENEMY else COLOR_PLAYER,
		true
	))

	private var direction: Float = 0f
	private var x: Float = 0f
	private var y: Float = 0f
	private var isAirborne: Boolean = false
	private var shootTime: Long = 0L

	private var moveDtMax = 1f
	private var lastMoveTime = 0L


	fun isIdle() = !isAirborne


	fun draw(): DrawCommandGroup {
		return DrawCommandGroup(
			x,
			y,
			0f,
			if (isAirborne) drawCommands else emptyList()
		)
	}


	fun hits(target: SpaceObject): Boolean {
		if (!isAirborne) {
			return false
		}

		val (targetX, targetY) = target.position()
		val distanceSquared = (x - targetX) * (x - targetX) + (y - targetY) * (y - targetY)
		val radiusSum = RADIUS + target.radius()
		return distanceSquared <= radiusSum * radiusSum
	}


	fun move(now: Long, viewportWidth: Float, viewportHeight: Float) {
		if (!isAirborne) {
			return
		}

		val deltaTime = ((now - lastMoveTime) / 1000f).coerceAtMost(moveDtMax)
		lastMoveTime = now

		val distance = SPEED * deltaTime
		val directionRad = Math.toRadians(direction.toDouble()).toFloat()
		x += distance * cos(directionRad)
		y += distance * sin(directionRad)

		if (canWrap) {
			isAirborne = shootTime + LIFETIME > now

			if (x < 0) x += viewportWidth
			if (y < 0) y += viewportHeight
			if (x > viewportWidth) x -= viewportWidth
			if (y > viewportHeight) y -= viewportHeight
		} else {
			isAirborne = x >= 0 && y >= 0 && x <= viewportWidth && y <= viewportHeight
		}
	}


	fun shoot(now: Long, fromPosition: Pair<Float, Float>, targetDirection: Float) {
		direction = targetDirection
		x = fromPosition.first
		y = fromPosition.second
		isAirborne = true
		shootTime = now

		lastMoveTime = now
		moveDtMax = 10f / Settings.Gameplay.TARGET_IPS.toFloat()
	}


	fun stop() {
		isAirborne = false
	}
}

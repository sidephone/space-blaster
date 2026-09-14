package com.sidephone.spaceblaster.engine.entities.ships

import com.sidephone.spaceblaster.engine.entities.HyperspaceJump
import com.sidephone.spaceblaster.engine.entities.SpaceObject
import com.sidephone.spaceblaster.engine.entities.asteroids.Asteroid
import com.sidephone.spaceblaster.engine.entities.getPlayerSpawnPosition
import com.sidephone.spaceblaster.engine.graphics.DrawCommandGroup
import com.sidephone.spaceblaster.settings.Settings.Player.INVINCIBILITY_DURATION
import com.sidephone.spaceblaster.settings.Settings.Player.RESPAWN_DELAY
import com.sidephone.spaceblaster.settings.Settings.Player.STARTING_LIVES
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

class PlayerShip : Ship() {
	private var isInvincible = false
	private var invincibilityTimeout = 0L // ms
	private var lastDeathTime = 0L

	private var beforeJumpX = 0f
	private var beforeJumpY = 0f
	private var jumpInEndTime = 0L
	private var jumpOutEndTime = 0L


	private val _lives = MutableStateFlow(STARTING_LIVES)
	val lives: StateFlow<Int> = _lives

	private val _isDeadForever = MutableStateFlow(false)
	val isDeadForever: StateFlow<Boolean> = _isDeadForever

	override fun notBumpable(now: Long) = super.notBumpable(now) || isInvincible || now < jumpOutEndTime
	override fun isDead(now: Long) = isDeadForever.value || (lastDeathTime + RESPAWN_DELAY > now)


	fun addLife() {
		if (isDeadForever.value) return
		_lives.value++
	}


	fun autoSpawnAfterDeath(now: Long, viewportWidth: Float, viewportHeight: Float) {
		if (isDeadForever.value || lastDeathTime == 0L) return

		if (now - lastDeathTime >= RESPAWN_DELAY) {
			spawn(now, viewportWidth, viewportHeight)
		}
	}


	fun burn(now: Long, other: SpaceObject): Boolean {
		if (!isThrusting || isDead(now)) return false

		// check if the other object is within range to be burned
		val dx = other.position().first - x
		val dy = other.position().second - y
		val distance = sqrt((dx * dx + dy * dy).toDouble())
		val burnDistance = other.radius() + shipType.radius() + shipType.thrustFireLength()

		if (distance > burnDistance) return false

		// check if the other object is behind the ship (i.e., within the thrust fire cone)
		val angleToOther = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())) + 180
		val angleDifference = (angleToOther - direction + 360) % 360
		val halfConeAngle = shipType.thrustFireHalfWidth()

		return angleDifference <= halfConeAngle || angleDifference >= 360 - halfConeAngle
	}


	fun burn(now: Long, asteroids: List<Asteroid>): Int {
		if (!isThrusting || isDead(now)) return -1

		for ((index, asteroid) in asteroids.withIndex()) {
			if (burn(now, asteroid)) {
				return index
			}
		}

		return -1
	}


	private fun calculateHyperspaceJumpScale(now: Long): Float {
		return if (now < jumpOutEndTime) {
			abs(now - jumpOutEndTime) / (HyperspaceJump.DURATION / 2f)
		} else if (now < jumpInEndTime) {
			1f - (jumpInEndTime - now) / (HyperspaceJump.DURATION / 2f)
		} else {
			1f
		}
	}


	private fun calculateDrawPosition(now: Long): Pair<Float, Float> {
		return if (now < jumpOutEndTime) {
			Pair(beforeJumpX, beforeJumpY)
		} else {
			Pair(x, y)
		}
	}


	override fun die(now: Long) {
		if (isDeadForever.value) return
		_lives.value--
		_isDeadForever.value = _lives.value <= 0
		lastDeathTime = now
	}


	override fun draw(now: Long): DrawCommandGroup {
		if (isDead(now)) {
			return DrawCommandGroup(0f, 0f, emptyList())
		}

		val blink = (isInvincible && (now % 200L < 100L))
		val (drawX, drawY) = calculateDrawPosition(now)

		return DrawCommandGroup(
			drawX,
			drawY,
			if (blink) emptyList() else shipType.draw(now, isThrusting),
			direction - shipType.drawDirection(),
			calculateHyperspaceJumpScale(now)
		)
	}


	fun draw(posX: Float, posY: Float, scale: Float): DrawCommandGroup {
		return DrawCommandGroup(
			posX,
			posY,
			shipType.draw(0, false),
			0f,
			scale
		)
	}


	fun jump(now: Long, viewportWidth: Float, viewportHeight: Float) {
		if (isDead(now)) return

		beforeJumpX = x
		beforeJumpY = y
		x = (Math.random() * viewportWidth).toFloat()
		y = (Math.random() * viewportHeight).toFloat()
		direction = (Math.random() * 360f).toFloat()
		speedX = 0f
		speedY = 0f
		jumpOutEndTime = now + HyperspaceJump.DURATION / 2
		jumpInEndTime = now + HyperspaceJump.DURATION
	}


	fun revokeInvincibilityWhenExpired(now: Long) {
		if (now >= invincibilityTimeout) {
			isInvincible = false
		}
	}


	fun resetLives() {
		_lives.value = STARTING_LIVES
		_isDeadForever.value = _lives.value <= 0
	}


	override fun spawn(now: Long, viewportWidth: Float, viewportHeight: Float) {
		if (isDeadForever.value) return

		super.spawn(now, viewportWidth, viewportHeight)
		isInvincible = true
		invincibilityTimeout = now + INVINCIBILITY_DURATION
		lastDeathTime = 0L

		beforeJumpX = 0f
		beforeJumpY = 0f
		jumpInEndTime = 0L
		jumpOutEndTime = 0L

		shipType = ShipTypeDefender()

		getPlayerSpawnPosition(viewportWidth, viewportHeight).also{
			x = it.x
			y = it.y
			direction = it.direction + shipType.drawDirection()
		}
	}
}

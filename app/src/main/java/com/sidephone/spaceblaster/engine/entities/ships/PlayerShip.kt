package com.sidephone.spaceblaster.engine.entities.ships

import com.sidephone.spaceblaster.engine.graphics.DrawCommandGroup
import com.sidephone.spaceblaster.settings.Settings.Player.INVINCIBILITY_DURATION
import com.sidephone.spaceblaster.settings.Settings.Player.RESPAWN_DELAY
import com.sidephone.spaceblaster.settings.Settings.Player.STARTING_LIVES
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PlayerShip : Ship() {
	private var isInvincible = false
	private var invincibilityTimeout = 0L // ms
	private var lastDeathTime = 0L

	private val _lives = MutableStateFlow(STARTING_LIVES)
	val lives: StateFlow<Int> = _lives

	private val _isDeadForever = MutableStateFlow(false)
	val isDeadForever: StateFlow<Boolean> = _isDeadForever

	override fun notBumpable(now: Long) = super.notBumpable(now) || isInvincible
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

		return DrawCommandGroup(
			x,
			y,
			if (blink) emptyList() else shipType.draw(now, isThrusting),
			direction - shipType.drawDirection(),
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


	fun revokeInvincibilityWhenExpired(now: Long) {
		if (now >= invincibilityTimeout) {
			isInvincible = false
		}
	}


	override fun resetLives() {
		_lives.value = STARTING_LIVES
		_isDeadForever.value = _lives.value <= 0
	}


	override fun spawn(now: Long, viewportWidth: Float, viewportHeight: Float) {
		if (isDeadForever.value) return

		super.spawn(now, viewportWidth, viewportHeight)
		isInvincible = true
		invincibilityTimeout = now + INVINCIBILITY_DURATION
		lastDeathTime = 0L
	}
}

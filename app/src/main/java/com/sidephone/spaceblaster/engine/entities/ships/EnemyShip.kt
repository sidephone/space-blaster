package com.sidephone.spaceblaster.engine.entities.ships

import com.sidephone.spaceblaster.engine.entities.getEnemySpawnPosition
import com.sidephone.spaceblaster.engine.graphics.DrawCommandGroup
import com.sidephone.spaceblaster.settings.Settings
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


class EnemyShip : Ship() {
	private var isDead = true
	private var nextDirectionChange = 0L
	private var lastStage = 0
	private var score = 0


	override fun isDead(now: Long) = isDead
	fun score() = score


	fun aim(target: Pair<Float, Float>) {
		if (isDead) return

		val (targetX, targetY) = target
		direction = Math.toDegrees(atan2((targetY - y).toDouble(), (targetX - x).toDouble())).toFloat()

		val accuracy = when (shipType) {
			is ShipTypeSaucerSmall -> ShipTypeSaucerSmall.AIM_ACCURACY
			is ShipTypeSaucerBig -> ShipTypeSaucerBig.AIM_ACCURACY
			else -> 0f
		}
		direction += (Math.random() * 2 - 1).toFloat() * (1 - accuracy) * 180f
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


	fun isTimeToSpawn(stage: Int, stageTime: Long, asteroidCount: Int): Boolean {
		return asteroidCount < Settings.Saucer.SPAWN_WHEN_MAX_ASTEROIDS
			&& isDead
			&& lastStage != stage
			&& stage >= Settings.Saucer.SPAWN_MIN_STAGE
			&& stageTime >= Settings.Saucer.SPAWN_STAGE_TIME_MIN
	}


	fun moveAtWill(now: Long, viewportWidth: Float, viewportHeight: Float) {
		if (isDead) return

		move(now, viewportWidth, viewportHeight)

		if (now < nextDirectionChange) {
			return
		}

		isThrusting = !isThrusting

		if (isThrusting) {
			val angle = 360 * Math.random()
			speedX = (shipType.maxSpeed() * cos(angle)).toFloat()
			speedY = (shipType.maxSpeed() * sin(angle)).toFloat()
			nextDirectionChange = now + Settings.Saucer.FLY_TIME_MIN + ((0..Settings.Saucer.FLY_TIME_MAX).random())
		} else {
			speedX = 0f
			speedY = 0f
			nextDirectionChange = now + Settings.Saucer.STILL_TIME_MIN + ((0..Settings.Saucer.STILL_TIME_MAX).random())
		}
	}


	fun reset() {
		lastStage = 0
		die(0)
	}


	override fun spawn(now: Long, viewportWidth: Float, viewportHeight: Float) {
		super.spawn(now, viewportWidth, viewportHeight)
		shipType = if ((0..1).random() == 0) ShipTypeSaucerBig() else ShipTypeSaucerSmall()

		getEnemySpawnPosition(viewportWidth, viewportHeight, shipType.radius()).let { (spawnX, spawnY) ->
			x = spawnX
			y = spawnY
		}
		isDead = false
		nextDirectionChange = 0L
		score = when (shipType) {
			is ShipTypeSaucerSmall -> ShipTypeSaucerSmall.SCORE_POINTS
			is ShipTypeSaucerBig -> ShipTypeSaucerBig.SCORE_POINTS
			else -> 0
		}
	}


	fun  spawnIfNeeded(now: Long, stage: Int, stageTime: Long, asteroidCount: Int, viewportWidth: Float, viewportHeight: Float) {
		if (isTimeToSpawn(stage, stageTime, asteroidCount)) {
			spawn(now, viewportWidth, viewportHeight)
			lastStage = stage
		}
	}
}

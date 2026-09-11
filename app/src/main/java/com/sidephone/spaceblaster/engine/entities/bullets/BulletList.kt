package com.sidephone.spaceblaster.engine.entities.bullets

import com.sidephone.spaceblaster.engine.entities.SpaceObject
import com.sidephone.spaceblaster.engine.entities.ships.EnemyShip
import com.sidephone.spaceblaster.engine.entities.ships.PlayerShip
import com.sidephone.spaceblaster.engine.graphics.DrawCommandGroup
import com.sidephone.spaceblaster.settings.Settings

abstract class BulletList {
	protected var bullets = mutableListOf<Bullet>()
	private var hitAsteroidId = -1
	private var hitEnemy = false
	private var hitPlayer = false
	private var hittingBulletDirection = 0f
	private var lastShootTime = 0L


	abstract fun resetBullets(settings: Settings?, stage: Int)
	abstract fun shootDelay(now: Long): Long


	fun draw(): List<DrawCommandGroup> {
		return bullets.filterNot { it.isIdle() }.map { it.draw() }
	}


	fun hitAsteroidId() = hitAsteroidId
	fun hitEnemy() = hitEnemy
	fun hitPlayer() = hitPlayer
	fun hittingBulletDirection() = hittingBulletDirection


	fun move(now: Long, targets: List<SpaceObject>, viewportWidth: Float, viewportHeight: Float) {
		hitAsteroidId = -1
		hitEnemy = false
		hitPlayer = false

		for (bullet in bullets) {
			bullet.move(now, viewportWidth, viewportHeight)

			for ((index, target) in targets.withIndex()) {
				if (bullet.hits(target)) {
					if (!bullet.isEnemy() && target is EnemyShip) {
						hitEnemy = !target.isDead(now)
					} else if (bullet.isEnemy() && target is PlayerShip) {
						hitPlayer = !target.isDead(now)
					} else {
						hitAsteroidId = index
					}
					hittingBulletDirection = bullet.direction()
					bullet.stop()
					break
				}
			}
		}
	}


	fun reset(settings: Settings?, stage: Int) {
		hitAsteroidId = -1
		resetBullets(settings, stage)
	}


	fun resetShootTime() {
		lastShootTime = 0L
	}


	fun shoot(now: Long, fromPosition: Pair<Float, Float>, direction: Float) {
		if (now - lastShootTime < shootDelay(now)) {
			return
		}

		for (bullet in bullets) {
			if (bullet.isIdle()) {
				bullet.shoot(now, fromPosition, direction)
				lastShootTime = now
				break
			}
		}
	}
}

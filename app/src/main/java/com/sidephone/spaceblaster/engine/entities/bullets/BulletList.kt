package com.sidephone.spaceblaster.engine.entities.bullets

import com.sidephone.spaceblaster.engine.entities.SpaceObject
import com.sidephone.spaceblaster.engine.entities.asteroids.Asteroid
import com.sidephone.spaceblaster.engine.entities.ships.EnemyShip
import com.sidephone.spaceblaster.engine.entities.ships.PlayerShip
import com.sidephone.spaceblaster.settings.Settings

abstract class BulletList {
	protected var bullets = mutableListOf<Bullet>()
	private var hittingBulletDirection = 0f
	protected var lastShootTime = 0L


	abstract fun resetBullets(settings: Settings?, stage: Int)
	abstract fun shootDelay(now: Long): Long


	fun draw() = bullets.filterNot { it.isIdle() }.map { it.draw() }
	fun hittingBulletDirection() = hittingBulletDirection
	fun reset(settings: Settings?, stage: Int) { resetBullets(settings, stage) }
	fun resetShootTime(now: Long) { lastShootTime = now }


	fun hit(now: Long, target: SpaceObject): Boolean {
		if (target.notBumpable(now)) {
			return false
		}

		for (bullet in bullets) {
			if (
				!bullet.hits(target)
				|| (bullet.isEnemy() && target is EnemyShip)
				|| (!bullet.isEnemy() && target is PlayerShip)
			) {
				continue
			}

			hittingBulletDirection = bullet.direction()
			bullet.stop()
			return true
		}

		return false
	}


	fun hit(now: Long, asteroids: List<Asteroid>): Int {
		for ((index, target) in asteroids.withIndex()) {
			if (hit(now, target)) {
				return index
			}
		}

		return -1
	}


	fun move(now: Long, viewportWidth: Float, viewportHeight: Float) {
		for (bullet in bullets) {
			bullet.move(now, viewportWidth, viewportHeight)
		}
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

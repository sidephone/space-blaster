package com.sidephone.spaceblaster.engine.entities.bullets

import com.sidephone.spaceblaster.engine.entities.SpaceObject
import com.sidephone.spaceblaster.engine.graphics.DrawCommandGroup
import com.sidephone.spaceblaster.settings.Settings

abstract class BulletList {
	protected var bullets = mutableListOf<Bullet>()
	private var hitsTarget = -1
	private var lastShootTime = 0L


	abstract fun resetBullets(settings: Settings?, stage: Int)
	abstract fun shootDelay(): Long


	fun draw(): List<DrawCommandGroup> {
		return bullets.filterNot { it.isIdle() }.map { it.draw() }
	}


	fun hitsTarget() = hitsTarget


	fun move(now: Long, targets: List<SpaceObject>, viewportWidth: Float, viewportHeight: Float) {
		hitsTarget = -1

		for (bullet in bullets) {
			bullet.move(now, viewportWidth, viewportHeight)

			for ((index, target) in targets.withIndex()) {
				if (bullet.hits(target)) {
					hitsTarget = index
					bullet.stop()
					break
				}
			}
		}
	}


	fun reset(settings: Settings?, stage: Int) {
		hitsTarget = -1
		resetBullets(settings, stage)
	}


	fun resetShootTime() {
		lastShootTime = 0L
	}


	fun shoot(now: Long, fromPosition: Pair<Float, Float>, direction: Float) {
		if (now - lastShootTime < shootDelay()) {
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

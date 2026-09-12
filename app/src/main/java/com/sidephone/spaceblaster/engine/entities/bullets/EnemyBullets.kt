package com.sidephone.spaceblaster.engine.entities.bullets

import com.sidephone.spaceblaster.settings.Settings
import com.sidephone.spaceblaster.settings.Settings.Saucer.BULLETS_MAX
import com.sidephone.spaceblaster.settings.Settings.Saucer.SHOOT_DELAY_MIN
import com.sidephone.spaceblaster.settings.Settings.Saucer.SHOOT_DELAY_MAX

class EnemyBullets : BulletList() {
	private var shootDelay = SHOOT_DELAY_MIN
	private var nextShootDelayRecalculate = 0L

	override fun resetBullets(settings: Settings?, stage: Int) {
		bullets.clear()
		repeat(BULLETS_MAX) {
			bullets.add(Bullet(true, settings?.getBulletsWrapAround() ?: false))
		}
	}

	override fun shootDelay(now: Long): Long {
		// first shoot is always after a while
		if (now - lastShootTime > SHOOT_DELAY_MAX) {
			shootDelay = SHOOT_DELAY_MAX
		}

		// subsequent shoots are randomized between min and max shoot delay
		if (now >= nextShootDelayRecalculate) {
			shootDelay = (SHOOT_DELAY_MIN..SHOOT_DELAY_MAX).random()
			nextShootDelayRecalculate = now + shootDelay
		}

		return shootDelay
	}
}

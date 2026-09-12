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
		if (now >= nextShootDelayRecalculate) {
			shootDelay = (SHOOT_DELAY_MIN..SHOOT_DELAY_MAX).random()
			nextShootDelayRecalculate = now + shootDelay
		}

		return shootDelay
	}
}

package com.sidephone.spaceblaster.engine.entities.bullets

import com.sidephone.spaceblaster.settings.Settings
import com.sidephone.spaceblaster.settings.Settings.PlayerBullets.BONUS_EVERY_N_STAGES
import com.sidephone.spaceblaster.settings.Settings.PlayerBullets.BONUS_SHOOT_DELAY_PER_STAGE
import com.sidephone.spaceblaster.settings.Settings.PlayerBullets.MIN
import com.sidephone.spaceblaster.settings.Settings.PlayerBullets.MAX
import com.sidephone.spaceblaster.settings.Settings.PlayerBullets.SHOOT_DELAY
import com.sidephone.spaceblaster.settings.Settings.PlayerBullets.SHOOT_DELAY_MIN

class PlayerBullets : BulletList() {
	private var shootDelay = SHOOT_DELAY


	override fun resetBullets(settings: Settings?, stage: Int) {
		shootDelay = (SHOOT_DELAY - (BONUS_SHOOT_DELAY_PER_STAGE * stage)).coerceAtLeast(SHOOT_DELAY_MIN)

		bullets.clear()
		val bulletCount = (MIN + (stage / BONUS_EVERY_N_STAGES)).coerceAtMost(MAX)
		repeat(bulletCount) {
			bullets.add(Bullet(false, settings?.getBulletsWrapAround() ?: false))
		}
	}

	override fun shootDelay() = shootDelay
}

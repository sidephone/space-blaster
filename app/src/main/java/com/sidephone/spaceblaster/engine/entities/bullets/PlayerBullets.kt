package com.sidephone.spaceblaster.engine.entities.bullets

import com.sidephone.spaceblaster.settings.Settings

class PlayerBullets : BulletList() {
	companion object {
		const val BULLET_BONUS_EVERY_N_STAGES = 10
		const val MIN = 4
		const val SHOOT_DELAY = 750L // ms
	}

	override fun resetBullets(settings: Settings?, stage: Int) {
		bullets.clear()
		repeat(MIN + (stage / BULLET_BONUS_EVERY_N_STAGES)) {
			bullets.add(Bullet(false, settings?.getBulletsWrapAround() ?: false))
		}
	}

	override fun shootDelay() = SHOOT_DELAY
}

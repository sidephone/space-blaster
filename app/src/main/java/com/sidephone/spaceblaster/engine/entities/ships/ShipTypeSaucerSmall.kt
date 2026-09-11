package com.sidephone.spaceblaster.engine.entities.ships

import com.sidephone.spaceblaster.engine.graphics.DrawCommand

class ShipTypeSaucerSmall : ShipType {
	companion object {
		const val MIN_SPEED = 25f // px/sec
		const val MAX_SPEED = 125f // px/sec

		const val DRAW_DIRECTION = -90f // degrees, 0 is to the right, -90 is straight up
		const val RADIUS = 35f
		const val SIZE_UNIT = RADIUS / 30f

		const val SCORE_POINTS = 10
	}

	override fun acceleration() = 0f
	override fun braking() = 0f
	override fun cannonLength() = RADIUS

	override fun draw(
		now: Long,
		thrusting: Boolean
	): List<DrawCommand> {
		return emptyList()
	}

	override fun drawDirection(): Float {
		return 0f
	}

	override fun maxSpeed(): Float {
		return 0f
	}

	override fun radius(): Float {
		return 0f
	}


	override fun turnSpeed(): Float {
		return 0f
	}
}

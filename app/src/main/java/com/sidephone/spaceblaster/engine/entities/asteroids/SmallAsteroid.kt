package com.sidephone.spaceblaster.engine.entities.asteroids

import com.sidephone.spaceblaster.engine.graphics.DrawCommand

class SmallAsteroid : AsteroidType() {
	companion object {
		const val RADIUS = 10f
		const val SPEED = 100f // px/sec
		const val TURN_SPEED = 60f // degrees/sec
		const val MASS = RADIUS * RADIUS

		const val SURFACE_COLOR = 0xFFCCCCCC.toInt()

		const val POINTS = 4
	}

	override fun mass(): Float {
		return MASS
	}

	override fun radius(): Float {
		return RADIUS
	}

	override fun speed(): Float {
		return SPEED
	}

	override fun turnSpeed(): Float {
		return TURN_SPEED
	}

	override fun turnsLeft(): Boolean {
		return true
	}

	override fun draw(): List<DrawCommand> {
		return listOf(
			DrawCommand.Circle(
				0f,
				0f,
				RADIUS,
				SURFACE_COLOR,
				true
			),
		)
	}
}

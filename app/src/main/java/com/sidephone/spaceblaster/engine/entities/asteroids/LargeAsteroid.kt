package com.sidephone.spaceblaster.engine.entities.asteroids

import com.sidephone.spaceblaster.engine.graphics.DrawCommand

class LargeAsteroid : AsteroidType {
	companion object {
		const val RADIUS = 35f
		const val SPEED = 50f // px/sec
		const val TURN_SPEED = 30f // degrees/sec
		const val MASS = RADIUS * RADIUS

		const val SURFACE_COLOR = 0xFF888888.toInt()

		const val POINTS = 1
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
			DrawCommand.Circle(0f, 0f, RADIUS, SURFACE_COLOR, true),
		)
	}
}

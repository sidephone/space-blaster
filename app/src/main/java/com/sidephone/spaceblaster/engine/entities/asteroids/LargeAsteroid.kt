package com.sidephone.spaceblaster.engine.entities.asteroids

import com.sidephone.spaceblaster.engine.graphics.DrawCommand

class LargeAsteroid : AsteroidType() {
	companion object {
		const val RADIUS = 35f
		const val SPEED = 50f // px/sec
		const val TURN_SPEED = 30f // degrees/sec
		const val MASS = RADIUS * RADIUS

		const val SURFACE_COLOR = 0xFF888888.toInt()

		const val POINTS = 1
	}

	private var drawCommands: List<DrawCommand> = emptyList()

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
		if (drawCommands.isEmpty()) {
			drawCommands = listOf(
				drawMainSurface(RADIUS, SURFACE_COLOR),
				drawPatch(
					cx = -RADIUS * 0.30f,
					cy = -RADIUS * 0.25f,
					radius = RADIUS * 0.22f,
					vertexCount = 5,
					rotateDeg = -8f,
					color = 0xFF626262.toInt(),
					filled = true
				),

				drawPatch(
					cx = RADIUS * 0.28f,
					cy = -RADIUS * 0.10f,
					radius = RADIUS * 0.18f,
					vertexCount = 6,
					rotateDeg = 12f,
					color = 0xFF666666.toInt(),
					filled = true
				),
				drawPatch(
					cx = RADIUS * 0.05f,
					cy = RADIUS * 0.32f,
					radius = RADIUS * 0.35f,
					vertexCount = 6,
					rotateDeg = -5f,
					color = 0xFF5C5C5C.toInt(),
					filled = true
				),
			)
		}

		return drawCommands
	}
}

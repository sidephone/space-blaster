package com.sidephone.spaceblaster.engine.entities.asteroids

import com.sidephone.spaceblaster.engine.graphics.DrawCommand

class AsteroidTypeSmall : AsteroidType() {
	companion object {
		const val RADIUS = 12f
		const val SPEED = 100f // px/sec
		const val MASS = RADIUS * RADIUS

		const val SURFACE_COLOR = 0xFFCCCCCC.toInt()

		const val SCORE_POINTS = 4
	}

	object Patch {
		const val MIN = 0
		const val MAX = 2
		const val COLOR_MIN = 0xA0
		const val COLOR_MAX = 0xB8
		const val RADIUS_MIN = RADIUS * 0.1f
		const val RADIUS_MAX = RADIUS * 0.5f
		const val VERTICES_MIN = 3
		const val VERTICES_MAX = 4
	}

	private val turnSpeed = (120 - 80 * Math.random()).toFloat() * (if (Math.random() < 0.5) 1f else -1f) // degrees/sec
	private var drawCommands: List<DrawCommand> = emptyList()


	override fun mass() = MASS
	override fun radius() = RADIUS
	override fun score() = SCORE_POINTS
	override fun speed() = SPEED
	override fun turnSpeed() = turnSpeed


	override fun draw(): List<DrawCommand> {
		if (drawCommands.isEmpty()) {
			drawCommands = listOf(drawMainSurface(RADIUS, SURFACE_COLOR)) + drawPatches(
					Patch.MIN,
					Patch.MAX,
					Patch.COLOR_MIN,
					Patch.COLOR_MAX,
					RADIUS,
					Patch.RADIUS_MIN,
					Patch.RADIUS_MAX,
					Patch.VERTICES_MIN,
					Patch.VERTICES_MAX
			)
		}

		return drawCommands
	}
}

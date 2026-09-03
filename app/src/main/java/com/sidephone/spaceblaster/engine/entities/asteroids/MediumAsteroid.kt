package com.sidephone.spaceblaster.engine.entities.asteroids

import com.sidephone.spaceblaster.engine.graphics.DrawCommand

class MediumAsteroid : AsteroidType() {
	companion object {
		const val RADIUS = 22f
		const val SPEED = 75f // px/sec
		const val MASS = RADIUS * RADIUS

		const val SURFACE_COLOR = 0xFFAAAAAA.toInt()

		const val POINTS = 2
	}

	object Patch {
		const val MIN = 2
		const val MAX = 7
		const val COLOR_MIN = 0x7C // will result in: 0xff7c7c7c
		const val COLOR_MAX = 0x88 // will result in: 0xff888888
		const val RADIUS_MIN = RADIUS * 0.1f
		const val RADIUS_MAX = RADIUS * 0.35f
		const val VERTICES_MIN = 4
		const val VERTICES_MAX = 6
	}

	private val turnSpeed = (90 - 60 * Math.random()).toFloat() * (if (Math.random() < 0.5) 1f else -1f) // degrees/sec
	private var drawCommands: List<DrawCommand> = emptyList()


	override fun mass() = MASS
	override fun radius() = RADIUS
	override fun speed() = SPEED
	override fun turnSpeed() = turnSpeed


	override fun draw(): List<DrawCommand> {
		if (drawCommands.isEmpty()) {
			drawCommands = listOf(
				drawMainSurface(RADIUS, SURFACE_COLOR)) +
				drawPatches(
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

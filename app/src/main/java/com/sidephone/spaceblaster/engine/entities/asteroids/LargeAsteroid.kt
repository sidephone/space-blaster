package com.sidephone.spaceblaster.engine.entities.asteroids

import com.sidephone.spaceblaster.engine.graphics.DrawCommand

class LargeAsteroid : AsteroidType() {
	companion object {
		const val RADIUS = 35f
		const val SPEED = 50f // px/sec
		const val MASS = RADIUS * RADIUS

		const val SURFACE_COLOR = 0xFF888888.toInt()

		const val POINTS = 1
	}



	object Patch {
		const val MIN = 3
		const val MAX = 6
		const val COLOR_MIN = 0x60 // will result in: 0xff606060
		const val COLOR_MAX = 0x70 // will result in: 0xff707070
		const val RADIUS_MIN = RADIUS * 0.1f
		const val RADIUS_MAX = RADIUS * 0.4f
		const val VERTICES_MIN = 5
		const val VERTICES_MAX = 6
	}

	private val turnSpeed = (15 - 30 * Math.random()).toFloat() // degrees/sec
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

package com.sidephone.spaceblaster.engine.entities.ships

import com.sidephone.spaceblaster.engine.graphics.DrawCommand

class DefenderShip : ShipType {
	companion object {
		const val ACCELERATION = 175f // px/sec^2
		const val BRAKING = ACCELERATION * 0.75f // px/sec^2
		const val MAX_SPEED = 250f // px/sec
		const val TURN_SPEED = 150f // degrees/sec

		const val DRAW_DIRECTION = -90f // degrees, 0 is to the right, -90 is straight up
		const val RADIUS = 30f
		const val SIZE_UNIT = RADIUS / 25.5f
	}

	object Cockpit {
		const val COLOR: Int = 0XFFE5E533.toInt()
		const val X: Float = 0f
		const val Y: Float = 5.5f * SIZE_UNIT
		const val RADIUS: Float = 3.5f * SIZE_UNIT
	}

	object Fuselage {
		const val COLOR: Int = 0XFF00B300.toInt()
		const val TOP_Y = -25.5f * SIZE_UNIT
		const val TOP_X = 0f
		const val NOSE_HALF_WIDTH = 6f * SIZE_UNIT
		const val NOSE_BOTTOM = TOP_Y + 21f * SIZE_UNIT
		const val CENTER_HALF_WIDTH = 10f * SIZE_UNIT
		const val CENTER_BOTTOM = NOSE_BOTTOM + 7f * SIZE_UNIT
		const val END_HALF_WIDTH = 18f * SIZE_UNIT
		const val END_BOTTOM = CENTER_BOTTOM + 8f * SIZE_UNIT
		const val WING_HOLDER_TOP = END_BOTTOM - 0.5f
		const val WING_HOLDER_HALF_WIDTH = 21f * SIZE_UNIT
		const val WING_HOLDER_HEIGHT = 5f * SIZE_UNIT
	}

	object Wing {
		const val COLOR = 0xffc20000.toInt()
		const val TOP = 1.5f * SIZE_UNIT
		const val INNER_X = 20f * SIZE_UNIT
		const val WIDTH = 4f * SIZE_UNIT
		const val HEIGHT = 24f * SIZE_UNIT
	}


	object Fire {
		const val COLOR = 0XFFFFD700.toInt()
		const val REAR_Y = Fuselage.WING_HOLDER_TOP + Fuselage.WING_HOLDER_HEIGHT
		const val HALF_WIDTH = Fuselage.CENTER_HALF_WIDTH
		const val PHASE_TIME = 60L // ms, time for each flame phase
		val LENGTHS = floatArrayOf(15f, 21f, 17f, 24f, 18f, 22f)
		val OFFSETS = floatArrayOf(0f, -1.5f, 1f, -2f, 2f, -0.5f)
	}

	private var drawCommandCache = emptyList<DrawCommand>()


	override fun drawDirection(): Float {
		// the model is drawn facing up, but since 0 degrees is to the right, we need to rotate it
		// to appear and fly in the correct direction
		return DRAW_DIRECTION
	}


	override fun acceleration(): Float {
		return ACCELERATION
	}


	override fun braking(): Float {
		return BRAKING
	}


	override fun maxSpeed(): Float {
		return MAX_SPEED
	}


	override fun turnSpeed(): Float {
		return TURN_SPEED
	}


	override fun radius(): Float {
		return RADIUS
	}


	override fun draw(now: Long, thrusting: Boolean): List<DrawCommand> {
		if (drawCommandCache.isEmpty()) {
			drawCommandCache = drawBody() + drawWings()
		}

		return drawCommandCache + if (thrusting) drawThrustFire(now) else emptyList()
	}


	private fun drawBody(): List<DrawCommand> {
		return listOf(
			// fuselage
			DrawCommand.Polygon(
				// drawn from top to bottom, right side first, then left side
				listOf(
					Pair(Fuselage.TOP_X, Fuselage.TOP_Y), // tip
					Pair(Fuselage.NOSE_HALF_WIDTH, Fuselage.NOSE_BOTTOM), // nose, right bottom
					Pair(Fuselage.CENTER_HALF_WIDTH, Fuselage.CENTER_BOTTOM), // fuselage center, right bottom
					Pair(Fuselage.END_HALF_WIDTH, Fuselage.END_BOTTOM), // fuselage end, right bottom
					Pair(-Fuselage.END_HALF_WIDTH, Fuselage.END_BOTTOM), // fuselage end, left bottom
					Pair(-Fuselage.CENTER_HALF_WIDTH, Fuselage.CENTER_BOTTOM), // fuselage center, left bottom
					Pair(-Fuselage.NOSE_HALF_WIDTH, Fuselage.NOSE_BOTTOM), // nose left bottom
				),
				0f,
				Fuselage.COLOR,
				filled = true
			),

			// wing holder
			DrawCommand.Rect(
				-Fuselage.WING_HOLDER_HALF_WIDTH, Fuselage.WING_HOLDER_TOP,
				Fuselage.WING_HOLDER_HALF_WIDTH, Fuselage.WING_HOLDER_TOP + Fuselage.WING_HOLDER_HEIGHT,
				0f,
				Fuselage.COLOR,
				filled = true
			),

			// cockpit
			DrawCommand.Circle(
				Cockpit.X, Cockpit.Y, Cockpit.RADIUS,
				Cockpit.COLOR,
				filled = true
			)
		)
	}


	private fun drawWings(): List<DrawCommand> {
		return listOf(
			// left
			DrawCommand.Rect(
				-Wing.INNER_X - Wing.WIDTH, Wing.TOP,
				-Wing.INNER_X, Wing.TOP + Wing.HEIGHT,
				0f,
				Wing.COLOR,
				filled = true
			),

			// right
			DrawCommand.Rect(
				Wing.INNER_X, Wing.TOP,
				Wing.INNER_X + Wing.WIDTH, Wing.TOP + Wing.HEIGHT,
				0f,
				Wing.COLOR,
				filled = true
			),
		)
	}


	private fun drawThrustFire(now: Long): List<DrawCommand> {
		val flamePhase = ((now / Fire.PHASE_TIME) % Fire.LENGTHS.size).toInt()

		return listOf(
			DrawCommand.Polygon(
				points = listOf(
					Pair(-Fire.HALF_WIDTH, Fire.REAR_Y),
					Pair(Fire.OFFSETS[flamePhase], Fire.REAR_Y + Fire.LENGTHS[flamePhase]),
					Pair(Fire.HALF_WIDTH, Fire.REAR_Y)
				),
				rotateDeg = 0f,
				color = Fire.COLOR,
				filled = true
			)
		)
	}
}

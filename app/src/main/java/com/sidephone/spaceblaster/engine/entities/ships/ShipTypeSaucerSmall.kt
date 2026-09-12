package com.sidephone.spaceblaster.engine.entities.ships

import com.sidephone.spaceblaster.engine.graphics.DrawCommand

class ShipTypeSaucerSmall : ShipTypeSaucerBig() {
	companion object {
		const val AIM_ACCURACY = 0.75f // 1.0 = perfect aim, 0.0 = random aim
		const val DRAW_DIRECTION = -90f // degrees, 0 is to the right, -90 is straight up
		const val SPEED = 175f // px/sec
		const val RADIUS = 25f
		const val SIZE_UNIT = RADIUS / 25f

		const val SCORE_POINTS = 75
	}

	object Color {
		const val HULL_DARK = 0xFF30363C.toInt()
		const val HULL = 0xFF606870.toInt()
		const val HULL_LIGHT = 0xFF9AA2AA.toInt()

		const val GLASS = 0xFF4FB7C5.toInt()
		const val GLASS_LIGHT = 0xFF9DE8EF.toInt()

		const val LIGHT = 0xFFFFD85A.toInt()
		const val ENGINE = 0xFF80D8FF.toInt()
	}


	override fun cannonLength() = RADIUS
	override fun drawDirection() = DRAW_DIRECTION
	override fun maxSpeed() = SPEED
	override fun radius() = RADIUS


	override fun drawBody(): List<DrawCommand> {
		return listOf(
			// Lower trapezoid.
			DrawCommand.Polygon(
				points = listOf(
					-25 * SIZE_UNIT to 0f,
					25 * SIZE_UNIT to 0f,
					17 * SIZE_UNIT to 9 * SIZE_UNIT,
					-17 * SIZE_UNIT to 9 * SIZE_UNIT
				),
				rotateDeg = 0f,
				color = Color.HULL_DARK,
				filled = true
			),

			// Upper trapezoid, sharing the same long side
			DrawCommand.Polygon(
				points = listOf(
					-17 * SIZE_UNIT to 0f,
					17 * SIZE_UNIT to 0f,
					11 * SIZE_UNIT to -8 * SIZE_UNIT,
					-11 * SIZE_UNIT to -8 * SIZE_UNIT
				),
				rotateDeg = 0f,
				color = Color.HULL,
				filled = true
			),

			// Upper edge highlight.
			DrawCommand.Line(
				x1 = -11 * SIZE_UNIT,
				y1 = -8 * SIZE_UNIT,
				x2 = 11 * SIZE_UNIT,
				y2 = -8 * SIZE_UNIT,
				color = Color.HULL_LIGHT
			),

			// Central cockpit.
			DrawCommand.Polygon(
				points = listOf(
					-9 * SIZE_UNIT to -1 * SIZE_UNIT,
					-6 * SIZE_UNIT to -5 * SIZE_UNIT,
					0f to -6 * SIZE_UNIT,
					6 * SIZE_UNIT to -5 * SIZE_UNIT,
					9 * SIZE_UNIT to -1 * SIZE_UNIT
				),
				rotateDeg = 0f,
				color = Color.GLASS,
				filled = true
			),

			// Cockpit reflection.
			DrawCommand.Line(
				x1 = -4 * SIZE_UNIT,
				y1 = -4 * SIZE_UNIT,
				x2 = 3 * SIZE_UNIT,
				y2 = -4 * SIZE_UNIT,
				color = Color.GLASS_LIGHT
			),

			// Two navigation lights.
			DrawCommand.Circle(
				cx = -18 * SIZE_UNIT,
				cy = 1 * SIZE_UNIT,
				radius = 1.5f * SIZE_UNIT,
				color = Color.LIGHT,
				filled = true
			),

			DrawCommand.Circle(
				cx = 18 * SIZE_UNIT,
				cy = 1 * SIZE_UNIT,
				radius = 1.5f * SIZE_UNIT,
				color = Color.LIGHT,
				filled = true
			)
		)
	}

	override fun drawEngine(): List<DrawCommand> {
		return listOf(
			DrawCommand.Polygon(
				points = listOf(
					-5 * SIZE_UNIT to 7 * SIZE_UNIT,
					5 * SIZE_UNIT to 7 * SIZE_UNIT,
					3 * SIZE_UNIT to 7 * SIZE_UNIT + 5 * SIZE_UNIT,
					0 * SIZE_UNIT to 7 * SIZE_UNIT + 5 * SIZE_UNIT + 2 * SIZE_UNIT,
					-3 * SIZE_UNIT to 7 * SIZE_UNIT + 5 * SIZE_UNIT
				),
				rotateDeg = 0f,
				color = Color.ENGINE,
				filled = true
			)
		)
	}
}

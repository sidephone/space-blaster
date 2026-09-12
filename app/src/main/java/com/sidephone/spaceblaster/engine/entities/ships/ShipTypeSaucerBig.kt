package com.sidephone.spaceblaster.engine.entities.ships

import com.sidephone.spaceblaster.engine.graphics.DrawCommand

open class ShipTypeSaucerBig : ShipType {
	companion object {
		const val AIM_ACCURACY = 0.0f // 1.0 = perfect aim, 0.0 = random aim
		const val DRAW_DIRECTION = -90f // degrees, 0 is to the right, -90 is straight up
		const val RADIUS = 35f
		const val SIZE_UNIT = RADIUS / 30f
		const val SPEED = 125f // px/sec

		const val SCORE_POINTS = 5
	}

	object Color {
		const val HULL_DARK = 0xFF30363C.toInt()
		const val HULL = 0xFF606870.toInt()
		const val HULL_LIGHT = 0xFF9AA2AA.toInt()
		const val HIGHLIGHT = 0xFFD5DBE0.toInt()

		const val GLASS = 0xFF4FB7C5.toInt()
		const val GLASS_LIGHT = 0xFF9DE8EF.toInt()

		const val LIGHT = 0xFFFFD85A.toInt()
		const val ENGINE = 0xFF80D8FF.toInt()
	}

	object Engine {
		const val LARGE_ARC_START_ANGLE = 15f
		const val LARGE_ARC_SWEEP_ANGLE = 150f
		const val SMALL_ARC_START_ANGLE = 20f
		const val SMALL_ARC_SWEEP_ANGLE = 140f
	}

	private var body = drawBody()
	private var engine = drawEngine()


	override fun acceleration() = 0f
	override fun braking() = 0f
	override fun cannonLength(): Float = RADIUS
	override fun drawDirection() = DRAW_DIRECTION
	override fun maxSpeed() = SPEED
	override fun radius() = RADIUS
	override fun turnSpeed() = 0f


	override fun draw(now: Long, thrusting: Boolean): List<DrawCommand> {
		return if (thrusting) engine + body else body
	}


	protected open fun drawBody(): List<DrawCommand> {
		return listOf(
			// Bottom shadow / underside.
			DrawCommand.Polygon(
				points = listOf(
					-27 * SIZE_UNIT to 0f,
					-22 * SIZE_UNIT to 5 * SIZE_UNIT,
					-14 * SIZE_UNIT to 8 * SIZE_UNIT,
					-6 * SIZE_UNIT to 10f,
					0f to 11 * SIZE_UNIT,
					6 * SIZE_UNIT to 10f,
					14 * SIZE_UNIT to 8 * SIZE_UNIT,
					22 * SIZE_UNIT to 5 * SIZE_UNIT,
					27 * SIZE_UNIT to 0f,
					21 * SIZE_UNIT to 3 * SIZE_UNIT,
					12 * SIZE_UNIT to 5 * SIZE_UNIT,
					0f to 6 * SIZE_UNIT,
					-12 * SIZE_UNIT to 5 * SIZE_UNIT,
					-21 * SIZE_UNIT to 3 * SIZE_UNIT
				),
				rotateDeg = 0f,
				color = Color.HULL_DARK,
				filled = true
			),

			// Main saucer body.
			// A wide polygon gives us the characteristic flattened UFO silhouette.
			DrawCommand.Polygon(
				points = listOf(
					-30 * SIZE_UNIT to 0f,
					-25 * SIZE_UNIT to -7 * SIZE_UNIT,
					-15 * SIZE_UNIT to -11 * SIZE_UNIT,
					0f to -13 * SIZE_UNIT,
					15 * SIZE_UNIT to -11 * SIZE_UNIT,
					25 * SIZE_UNIT to -7 * SIZE_UNIT,
					30 * SIZE_UNIT to 0f,
					24 * SIZE_UNIT to 7 * SIZE_UNIT,
					14 * SIZE_UNIT to 10 * SIZE_UNIT,
					0f to 12 * SIZE_UNIT,
					-14 * SIZE_UNIT to 10 * SIZE_UNIT,
					-24 * SIZE_UNIT to 7 * SIZE_UNIT
				),
				rotateDeg = 0f,
				color = Color.HULL,
				filled = true
			),

			// Dark lower rim.
			DrawCommand.Arc(
				cx = 0f,
				cy = SIZE_UNIT,
				radius = 9 * SIZE_UNIT,
				startAngle = 0f,
				sweepAngle = 180f,
				color = Color.HULL_DARK,
				filled = false
			),

			// Upper hull highlight.
			DrawCommand.Arc(
				cx = 0f,
				cy = -1 * SIZE_UNIT,
				radius = 27 * SIZE_UNIT,
				startAngle = 190f,
				sweepAngle = 160f,
				color = Color.HULL_LIGHT,
				filled = false
			),

			// Central cockpit dome.
			DrawCommand.Polygon(
				points = listOf(
					-12 * SIZE_UNIT to -6 * SIZE_UNIT,
					-9 * SIZE_UNIT to -10 * SIZE_UNIT,
					-5 * SIZE_UNIT to -13 * SIZE_UNIT,
					0f to -14 * SIZE_UNIT,
					5 * SIZE_UNIT to -13 * SIZE_UNIT,
					9 * SIZE_UNIT to -10 * SIZE_UNIT,
					12 * SIZE_UNIT to -6 * SIZE_UNIT
				),
				rotateDeg = 0f,
				color = Color.GLASS,
				filled = true
			),

			// Glass dome highlight.
			DrawCommand.Arc(
				cx = 0f,
				cy = -7 * SIZE_UNIT,
				radius = 9 * SIZE_UNIT,
				startAngle = 190f,
				sweepAngle = 160f,
				color = Color.GLASS,
				filled = true
			),

			// Bright reflection on the cockpit.
			DrawCommand.Arc(
				cx = -3 * SIZE_UNIT,
				cy = -9 * SIZE_UNIT,
				radius = 5 * SIZE_UNIT,
				startAngle = 200f,
				sweepAngle = 90f,
				color = Color.GLASS_LIGHT,
				filled = false
			),

			// Bright upper hull strip.
			DrawCommand.Line(
				x1 = -17 * SIZE_UNIT,
				y1 = -7 * SIZE_UNIT,
				x2 = 17 * SIZE_UNIT,
				y2 = -7 * SIZE_UNIT,
				color = Color.HIGHLIGHT
			),

			// Navigation lights around the rim.
			DrawCommand.Circle(
				cx = -20 * SIZE_UNIT,
				cy = SIZE_UNIT,
				radius = 2 * SIZE_UNIT,
				color = Color.LIGHT,
				filled = true
			),

			DrawCommand.Circle(
				cx = -10 * SIZE_UNIT,
				cy = 7 * SIZE_UNIT,
				radius = 1.5f * SIZE_UNIT,
				color = Color.LIGHT,
				filled = true
			),

			DrawCommand.Circle(
				cx = 10 * SIZE_UNIT,
				cy = 7 * SIZE_UNIT,
				radius = 1.5f * SIZE_UNIT,
				color = Color.LIGHT,
				filled = true
			),

			DrawCommand.Circle(
				cx = 20 * SIZE_UNIT,
				cy = SIZE_UNIT,
				radius = 2 * SIZE_UNIT,
				color = Color.LIGHT,
				filled = true
			),


			// Small central underside light.
			DrawCommand.Circle(
				cx = 0f,
				cy = 7 * SIZE_UNIT,
				radius = 2 * SIZE_UNIT,
				color = Color.LIGHT,
				filled = true
			)
		)
	}


	protected open fun drawEngine(): List<DrawCommand> {
		return listOf<DrawCommand>(
			DrawCommand.Arc(
				cx = 0f,
				cy = 10 * SIZE_UNIT,
				radius = 9 * SIZE_UNIT,
				startAngle = Engine.LARGE_ARC_START_ANGLE,
				sweepAngle = Engine.LARGE_ARC_SWEEP_ANGLE,
				color = Color.ENGINE,
				filled = true
			),
			DrawCommand.Arc(
				cx = -12 * SIZE_UNIT,
				cy = 9 * SIZE_UNIT,
				radius = 3 * SIZE_UNIT,
				startAngle = Engine.SMALL_ARC_START_ANGLE,
				sweepAngle = Engine.SMALL_ARC_SWEEP_ANGLE,
				color = Color.ENGINE,
				filled = true
			),
			DrawCommand.Arc(
				cx = 12 * SIZE_UNIT,
				cy = 9 * SIZE_UNIT,
				radius = 3 * SIZE_UNIT,
				startAngle = Engine.SMALL_ARC_START_ANGLE,
				sweepAngle = Engine.SMALL_ARC_SWEEP_ANGLE,
				color = Color.ENGINE,
				filled = true
			)
		)
	}
}

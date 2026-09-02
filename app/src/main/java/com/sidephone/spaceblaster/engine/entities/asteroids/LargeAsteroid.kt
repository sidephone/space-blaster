package com.sidephone.spaceblaster.engine.entities.asteroids

import com.sidephone.spaceblaster.engine.graphics.DrawCommand
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class LargeAsteroid : AsteroidType {
	companion object {
		const val RADIUS = 35f
		const val SPEED = 50f // px/sec
		const val TURN_SPEED = 30f // degrees/sec
		const val MASS = RADIUS * RADIUS

		const val SURFACE_COLOR = 0xFF888888.toInt()

		const val POINTS = 1
	}

	private val outline = generateAsteroidPoints(0f, 0f, RADIUS, 12)
	val detail1 = generateAsteroidPoints(
		cx = -RADIUS * 0.30f,
		cy = -RADIUS * 0.25f,
		radius = RADIUS * 0.22f,
		vertexCount = 5
	)

	val detail2 = generateAsteroidPoints(
		cx = RADIUS * 0.28f,
		cy = -RADIUS * 0.10f,
		radius = RADIUS * 0.18f,
		6
	)

	val detail3 = generateAsteroidPoints(
		cx = RADIUS * 0.05f,
		cy = RADIUS * 0.32f,
		radius = RADIUS * 0.35f,
		6
	)

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
			DrawCommand.Polygon(
				points = outline,
				rotateDeg = 0f,
				color = 0xFF858585.toInt(),
				filled = true
			),

			DrawCommand.Polygon(detail1, -8f, 0xFF626262.toInt(), true),
			DrawCommand.Polygon(detail2, 12f, 0xFF666666.toInt(), true),
			DrawCommand.Polygon(detail3, -5f, 0xFF5C5C5C.toInt(), true),
		)
	}


	private fun generateAsteroidPoints(
		cx: Float,
		cy: Float,
		radius: Float,
		vertexCount: Int,
	): List<Pair<Float, Float>> {
		val random = Random.Default
		val minRadius = radius * 0.95f
		val maxRadius = radius * 1.25f

		// Maximum angular deviation from the evenly spaced position.
		val angleJitter = (360f / vertexCount) * 0.25f

		return (0 until vertexCount).map { i ->
			// Evenly distribute vertices around the circle, then perturb
			// each angle slightly.
			val baseAngle = i * 360f / vertexCount
			val jitter = (random.nextFloat() * 2f - 1f) * angleJitter
			val angle = baseAngle + jitter

			// Bias the random radius toward smaller values. Most vertices
			// will therefore be relatively close to minRadius, with occasional
			// larger protrusions.
			val t = random.nextFloat()
			val radiusFactor = t * t

			val r = minRadius + (maxRadius - minRadius) * radiusFactor

			val radians = Math.toRadians(angle.toDouble())

			val x = cx + cos(radians).toFloat() * r
			val y = cy + sin(radians).toFloat() * r

			x to y
		}
	}
}

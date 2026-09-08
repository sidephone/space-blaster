package com.sidephone.spaceblaster.engine.graphics

import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

sealed class DrawCommand {
	data class Arc(val cx: Float, val cy: Float, val radius: Float, val startAngle: Float, val sweepAngle: Float, val color: Int, val filled: Boolean) : DrawCommand()
	data class Circle(val cx: Float, val cy: Float, val radius: Float, val color: Int, val filled: Boolean) : DrawCommand()
	data class Dot(val x: Float, val y: Float, val color: Int) : DrawCommand()
	data class Line(val x1: Float, val y1: Float, val x2: Float, val y2: Float, val color: Int) : DrawCommand()
	data class Polygon(val points: List<Pair<Float, Float>>, val rotateDeg: Float, val color: Int, val filled: Boolean) : DrawCommand()
	data class Rect(val left: Float, val top: Float, val right: Float, val bottom: Float, val rotateDeg: Float, val color: Int, val filled: Boolean) : DrawCommand()
	data class Text(val text: String, val x: Float, val y: Float, val textSize: Float, val color: Int) : DrawCommand()


	companion object {
		/**
		 * Generates a polygon that looks like a beaten-up circle with a random outline.
		 */
		fun randomPolygon(cx: Float, cy: Float, radius: Float, vertexCount: Int, rotateDeg: Float, color: Int, filled: Boolean): DrawCommand {
			return Polygon(
				generateRandomPolygonPoints(cx, cy, radius, vertexCount),
				rotateDeg,
				color,
				filled
			)
		}

		private fun generateRandomPolygonPoints(
			cx: Float,
			cy: Float,
			radius: Float,
			vertexCount: Int,
		): List<Pair<Float, Float>> {
			val random = Random.Default
			val maxRadius = radius * 1.25f
			val minRadius = radius * 0.95f  // keep the minimum closer to the enclosing circle to
			// avoid collisions apparently happening from a distance

			// Maximum angular deviation from the evenly spaced position.
			val angleJitter = (360f / vertexCount) * 0.4f

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
}

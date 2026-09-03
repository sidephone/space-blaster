package com.sidephone.spaceblaster.engine.entities.asteroids

import com.sidephone.spaceblaster.engine.graphics.DrawCommand
import kotlin.math.ceil

abstract class AsteroidType {
	abstract fun mass(): Float
	abstract fun radius(): Float
	abstract fun speed(): Float
	abstract fun turnSpeed(): Float
	abstract fun draw(): List<DrawCommand>


	protected fun drawMainSurface(radius: Float, color: Int): DrawCommand {
		return DrawCommand.randomPolygon(0f, 0f, radius, ceil(radius / 3).toInt().coerceAtLeast(5), 0f, color, true)
	}


	protected fun drawPatches(
		min: Int,
		max: Int,
		colorMin: Int,
		colorMax: Int,
		surfaceRadius: Float,
		radiusMin: Float,
		radiusMax: Float,
		verticesMin: Int,
		verticesMax: Int
	): List<DrawCommand> {
		val patchCount = (Math.random() * (max - min + 1) + min).toInt()

		val drawCommands = mutableListOf<DrawCommand>()

		repeat(patchCount) {
			val radius = (Math.random() * (radiusMax - radiusMin) + radiusMin).toFloat()
			val cx = (surfaceRadius - radius * 1.6f) * Math.random() * (if (Math.random() < 0.5) -1 else 1)
			val cy = (surfaceRadius - radius * 1.6f) * Math.random() * (if (Math.random() < 0.5) -1 else 1)

			val colorIndex = (colorMin + Math.random() * (colorMax - colorMin + 1)).toInt()
			val color = (0xFF shl 24) or (colorIndex shl 16) or (colorIndex shl 8) or colorIndex

			drawCommands.add(drawPatch(
				cx = cx.toFloat(),
				cy = cy.toFloat(),
				radius = radius,
				vertexCount = (Math.random() * (verticesMax - verticesMin + 1) + verticesMin).toInt(),
				rotateDeg = (Math.random() * 360).toFloat(),
				color = color
			))
		}

		return drawCommands
	}

	private fun drawPatch(cx: Float, cy: Float, radius: Float, rotateDeg: Float, vertexCount: Int, color: Int): DrawCommand {
		return DrawCommand.randomPolygon(cx, cy, radius, vertexCount, rotateDeg, color, true)
	}
}

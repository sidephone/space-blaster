package com.sidephone.spaceblaster.engine.entities.asteroids

import com.sidephone.spaceblaster.engine.graphics.DrawCommand
import kotlin.math.ceil

abstract class AsteroidType {
	abstract fun mass(): Float
	abstract fun radius(): Float
	abstract fun speed(): Float
	abstract fun turnSpeed(): Float
	abstract fun turnsLeft(): Boolean
	abstract fun draw(): List<DrawCommand>


	protected fun drawMainSurface(radius: Float, color: Int): DrawCommand {
		return DrawCommand.randomPolygon(0f, 0f, radius, ceil(radius / 3).toInt(), 0f, color, true)
	}

	protected fun drawPatch(cx: Float, cy: Float, radius: Float, rotateDeg: Float, vertexCount: Int, color: Int, filled: Boolean): DrawCommand {
		return DrawCommand.randomPolygon(cx, cy, radius, vertexCount, rotateDeg, color, filled)
	}
}

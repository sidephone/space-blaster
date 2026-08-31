package com.sidephone.spaceblaster.engine.graphics

sealed class DrawCommand {
	data class Arc(val cx: Float, val cy: Float, val radius: Float, val startAngle: Float, val sweepAngle: Float, val color: Int, val filled: Boolean) : DrawCommand()
	data class Circle(val cx: Float, val cy: Float, val radius: Float, val color: Int, val filled: Boolean) : DrawCommand()
	data class Dot(val x: Float, val y: Float, val color: Int) : DrawCommand()
	data class Line(val x1: Float, val y1: Float, val x2: Float, val y2: Float, val color: Int) : DrawCommand()
	data class Polygon(val points: List<Pair<Float, Float>>, val rotateDeg: Float, val color: Int, val filled: Boolean) : DrawCommand()
	data class Rect(val left: Float, val top: Float, val right: Float, val bottom: Float, val rotateDeg: Float, val color: Int, val filled: Boolean) : DrawCommand()
}

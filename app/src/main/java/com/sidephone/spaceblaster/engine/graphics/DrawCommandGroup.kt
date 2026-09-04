package com.sidephone.spaceblaster.engine.graphics

/**
 * A group of draw commands that can be transformed together.
 */
data class DrawCommandGroup(
	val x: Float, // transformation origin x
	val y: Float, // transformation origin y
	val commands: List<DrawCommand>, // the list of the commands to be drawn and transformed together
	val rotationDegrees: Float = 0f,
	val scale: Float = 1f
)

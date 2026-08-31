package com.sidephone.spaceblaster.engine.graphics

/**
 * A group of draw commands that can be transformed together.
 */
data class DrawCommandGroup(
	val x: Float, // transformation origin x
	val y: Float, // transformation origin y
	val rotationDegrees: Float,
	val commands: List<DrawCommand> // the list of the commands to be drawn and transformed together
)

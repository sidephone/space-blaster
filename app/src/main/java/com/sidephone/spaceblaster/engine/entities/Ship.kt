package com.sidephone.spaceblaster.engine.entities

import com.sidephone.spaceblaster.engine.entities.ships.DefenderShip
import com.sidephone.spaceblaster.engine.entities.ships.ShipTypeInterface
import com.sidephone.spaceblaster.engine.graphics.DrawCommand
import com.sidephone.spaceblaster.engine.graphics.DrawCommandGroup
import com.sidephone.spaceblaster.settings.Settings
import kotlin.math.cos
import kotlin.math.sin


class Ship {
	private var shipType: ShipTypeInterface = DefenderShip()
	private var drawCommands: List<DrawCommand> = listOf()

	private var direction: Float = 0f // degrees, 0 is to the right, -90 is straight up
	private var x: Float = 0f // px, center of the ship
	private var y: Float = 0f // px, center of the ship
	var moveStepMax: Float = 1f
	var turnStepMax: Float = 1f

	private var lastTurnTime = 0L // ms
	private var lastMoveTime = 0L // ms


	fun spawn(viewportWidth: Float, viewportHeight: Float) {
		shipType = DefenderShip()

		x = viewportWidth / 2f
		y = viewportHeight / 2f
		direction = shipType.drawDirection()
		moveStepMax = shipType.moveSpeed() / Settings.TARGET_IPS.toFloat()
		turnStepMax = shipType.turnSpeed() / Settings.TARGET_IPS.toFloat()

		drawCommands = shipType.drawCommands()
	}


	fun moveForward(now: Long, viewportWidth: Float, viewportHeight: Float) {
		val moveSpeed = (shipType.moveSpeed() * (now - lastMoveTime) / 1000f).coerceAtMost(moveStepMax)
		lastMoveTime = now

		val angle = Math.toRadians(direction.toDouble())
		x += moveSpeed * cos(angle).toFloat()
		y += moveSpeed * sin(angle).toFloat()

		// wrap around the screen edges
		if (x < 0) x = viewportWidth
		if (y < 0) y = viewportHeight
		if (x > viewportWidth) x = 0f
		if (y > viewportHeight) y = 0f
	}


	fun turn(now: Long, left: Boolean) {
		val turnSpeed = (shipType.turnSpeed() * (now - lastTurnTime) / 1000f).coerceAtMost(turnStepMax)
		lastTurnTime = now

		direction += if (left) -turnSpeed else turnSpeed
	}


	fun draw(): DrawCommandGroup {
		return DrawCommandGroup(x, y, direction - shipType.drawDirection(), drawCommands)
	}
}

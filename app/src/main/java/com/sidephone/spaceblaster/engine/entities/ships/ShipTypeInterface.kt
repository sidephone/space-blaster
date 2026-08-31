package com.sidephone.spaceblaster.engine.entities.ships

import com.sidephone.spaceblaster.engine.graphics.DrawCommand

interface ShipTypeInterface {
	fun drawDirection(): Float
	fun moveSpeed(): Float
	fun turnSpeed(): Float
	fun radius(): Float
	fun drawCommands(): List<DrawCommand>
}

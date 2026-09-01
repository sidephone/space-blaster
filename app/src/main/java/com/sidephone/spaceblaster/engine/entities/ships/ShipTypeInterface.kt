package com.sidephone.spaceblaster.engine.entities.ships

import com.sidephone.spaceblaster.engine.graphics.DrawCommand

interface ShipTypeInterface {
	fun drawDirection(): Float
	fun acceleration(): Float
	fun braking(): Float
	fun turnSpeed(): Float
	fun radius(): Float
	fun draw(now: Long, thrusting: Boolean): List<DrawCommand>
}

package com.sidephone.spaceblaster.engine.entities.ships

import com.sidephone.spaceblaster.engine.graphics.DrawCommand

interface ShipType {
	fun acceleration(): Float
	fun braking(): Float
	fun cannonLength(): Float
	fun draw(now: Long, thrusting: Boolean): List<DrawCommand>
	fun drawDirection(): Float
	fun maxSpeed(): Float
	fun radius(): Float
	fun turnSpeed(): Float
}

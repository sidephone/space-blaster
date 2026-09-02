package com.sidephone.spaceblaster.engine.entities.ships

import com.sidephone.spaceblaster.engine.graphics.DrawCommand

interface ShipType {
	fun drawDirection(): Float
	fun acceleration(): Float
	fun braking(): Float
	fun maxSpeed(): Float
	fun turnSpeed(): Float
	fun radius(): Float
	fun draw(now: Long, thrusting: Boolean): List<DrawCommand>
}

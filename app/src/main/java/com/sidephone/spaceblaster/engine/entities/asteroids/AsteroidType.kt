package com.sidephone.spaceblaster.engine.entities.asteroids

import com.sidephone.spaceblaster.engine.graphics.DrawCommand

interface AsteroidType {
	fun mass(): Float
	fun radius(): Float
	fun speed(): Float
	fun turnSpeed(): Float
	fun turnsLeft(): Boolean
	fun draw(): List<DrawCommand>
}

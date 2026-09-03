package com.sidephone.spaceblaster.engine.entities

interface SpaceObject {
	fun notBumpable(now: Long): Boolean
	fun position(): Pair<Float, Float>
	fun radius(): Float
	fun speed(): Pair<Float, Float>
}

package com.sidephone.spaceblaster.engine.entities

interface SpaceObject {
	fun isDead(): Boolean
	fun position(): Pair<Float, Float>
	fun radius(): Float
	fun speed(): Pair<Float, Float>
}

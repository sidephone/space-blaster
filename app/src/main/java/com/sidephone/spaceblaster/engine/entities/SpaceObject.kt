package com.sidephone.spaceblaster.engine.entities

import kotlin.math.sqrt

interface SpaceObject {
	fun notBumpable(now: Long): Boolean
	fun position(): Pair<Float, Float>
	fun radius(): Float
	fun speed(): Pair<Float, Float>


	/**
	 * Checks whether this object and the other object are close enough to collide (distance between
	 * centers <= sum of radii) AND are currently approaching each other. If they are overlapping but
	 * separating, we are forgiving and do not crash them.
	 */
	fun shouldBump(now: Long, ourSpeedX: Float, ourSpeedY: Float, other: SpaceObject): Boolean {
		if (notBumpable(now) || other.notBumpable(now)) return false

		val dx = other.position().first - position().first
		val dy = other.position().second - position().second
		val centerDistance = sqrt(dx * dx + dy * dy)
		val surfaceDistance = radius() + other.radius()

		if (centerDistance > surfaceDistance) {
			return false
		}

		// Approaching if the relative velocity, projected onto the line
		// connecting the two centers, points from us toward the other
		// (i.e. the distance between them is decreasing).
		val relativeSpeedX = ourSpeedX - other.speed().first
		val relativeSpeedY = ourSpeedY - other.speed().second
		val closingSpeed = relativeSpeedX * dx + relativeSpeedY * dy

		return closingSpeed > 0f
	}
}

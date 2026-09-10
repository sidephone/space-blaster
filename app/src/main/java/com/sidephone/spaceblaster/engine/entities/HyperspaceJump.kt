package com.sidephone.spaceblaster.engine.entities

import com.sidephone.spaceblaster.engine.graphics.DrawCommand
import com.sidephone.spaceblaster.engine.graphics.DrawCommandGroup
import kotlin.math.cos
import kotlin.math.sin

class HyperspaceJump {
	companion object {
		const val DURATION = 500L // ms
		const val RADIUS = 35f // px
	}

	object Color {
		const val CORE = 0xFFFFFFFF.toInt()
		const val BRIGHT = 0xFF80D8FF.toInt()
		const val MID = 0xFF3B9DFF.toInt()
		const val DARK = 0xFF1650A0.toInt()
	}

	object Core {
		const val RADIUS_UNITS = 0.08f // relative to the radius in the current frame
		const val GLOW_RADIUS_UNITS = 0.18f // relative to the radius in the current frame
	}

	object PerspectiveStreak {
		const val COUNT = 14
		const val LENGTH = RADIUS / 3.5f
		const val LENGTH_PHASE_VARIATION = 0.35f
		const val LENGTH_PHASE_INVARIABLE_LENGTH = (1f - LENGTH_PHASE_VARIATION)
		const val LENGTH_MOVEMENT_VARIATION = 0.85f
		const val LENGTH_MOVEMENT_INVARIABLE_LENGTH = (1f - LENGTH_MOVEMENT_VARIATION)
		const val PHASE = Ring.ROTATION / 100f // degrees per streak
		const val START_DISTANCE_UNITS = 0.95f // relative to the radius in the current frame
		const val START_DISTANCE_VARIATION = 0.55f // relative to the radius in the current frame
	}

	object Ring {
		const val COUNT = 4
		const val RADIUS_UNITS = 0.45f // relative to the radius in the current frame
		const val RADIUS_INCREMENT_UNITS = 0.18f // relative to the radius in the current frame
		const val ROTATION = 37f // degrees per ring
		const val SWEEP_ANGLE = 250f // degrees
		const val SWEEP_DECREMENT = 25f // degrees per ring
	}

	object Streak {
		const val COUNT = 12
		const val MIN_LENGTH_UNIT = 0.55f // relative to the radius in the current frame
		const val MAX_LENGTH_UNIT = 0.82f // relative to the radius in the current frame
		const val MAX_LENGTH_VARIATION = 0.06f // relative to the radius in the current frame
	}

	private var startX = 0f
	private var startY = 0f
	private var endX = 0f
	private var endY = 0f
	private var startTime = 0L
	private var endTime = 0L


	fun enterHyperspace(now: Long, position: Pair<Float, Float>) {
		startX = position.first
		startY = position.second
		startTime = now
		endTime = now + DURATION
	}


	fun exitHyperspace(position: Pair<Float, Float>) {
		endX = position.first
		endY = position.second
	}


	fun draw(now: Long): List<DrawCommandGroup>? {
		return if (now < endTime) {
			listOf(
				DrawCommandGroup(startX, startY, drawEnterPortal(now)),
				DrawCommandGroup(endX, endY, drawExitPortal(now))
			)
		} else {
			null
		}
	}


	private fun animationProgress(now: Long): Float {
		return ((now - startTime).toFloat() / DURATION).coerceIn(0f, 1f)
	}


	/**
	 * Entrance starts fully open and collapses to nothing.
	 */
	private fun drawEnterPortal(now: Long): List<DrawCommand> {
		val progress = animationProgress(now)
		val radius = RADIUS * (1f - progress)

		if (radius <= 0f) {
			return emptyList()
		}

		return drawPortal(radius, progress, true) + drawPerspectiveStreaks(radius, progress, true)
	}


	/**
	 * Exit starts at nothing and expands to a fully open portal.
	 */
	private fun drawExitPortal(now: Long): List<DrawCommand> {
		val progress = animationProgress(now)
		val radius = RADIUS * progress

		if (radius <= 0f) {
			return emptyList()
		}

		return drawPortal(radius, progress, false) + drawPerspectiveStreaks(radius, progress, false)
	}


	private fun drawPerspectiveStreaks(radius: Float, progress: Float, closing: Boolean): List<DrawCommand> {
		val commands = mutableListOf<DrawCommand>()

		for (i in 0 until PerspectiveStreak.COUNT) {
			val angleRad = Math.PI * (2.0 * i / PerspectiveStreak.COUNT + if (closing) -progress else progress)
			val movement = if (closing) 1f - progress else progress
			val phase = (i * PerspectiveStreak.PHASE) % 1f
			val startDistance = radius * (PerspectiveStreak.START_DISTANCE_UNITS + phase * PerspectiveStreak.START_DISTANCE_VARIATION)

			// streaks become more pronounced as the portal opens, or disappear as it closes
			val movementEffect = (PerspectiveStreak.LENGTH_MOVEMENT_INVARIABLE_LENGTH + movement * PerspectiveStreak.LENGTH_MOVEMENT_VARIATION)
			val phaseEffect = (PerspectiveStreak.LENGTH_PHASE_INVARIABLE_LENGTH + phase * PerspectiveStreak.LENGTH_PHASE_VARIATION)
			val length = PerspectiveStreak.LENGTH * movementEffect * phaseEffect

			commands += radialLine(
				angleRad = angleRad.toFloat(),
				innerRadius = startDistance,
				outerRadius = startDistance + length,
				color = when {
					i % 5 == 0 -> Color.BRIGHT
					i % 2 == 0 -> Color.MID
					else -> Color.DARK
				}
			)
		}

		return commands
	}


	private fun drawPortal(radius: Float, progress: Float, closing: Boolean): List<DrawCommand> {
		val commands = mutableListOf<DrawCommand>()

		// central glow
		commands += DrawCommand.Circle(
			cx = 0f,
			cy = 0f,
			radius = radius * Core.GLOW_RADIUS_UNITS,
			color = Color.BRIGHT,
			filled = true
		)

		// concentric rings
		for (i in 0 until Ring.COUNT) {
			val rotation = Ring.ROTATION * i + 360f * if (closing) -progress else progress

			commands += DrawCommand.Arc(
				cx = 0f,
				cy = 0f,
				radius = radius * (Ring.RADIUS_UNITS + i * Ring.RADIUS_INCREMENT_UNITS),
				startAngle = rotation,
				sweepAngle = Ring.SWEEP_ANGLE - i * Ring.SWEEP_DECREMENT,
				color = when (i) {
					0 -> Color.CORE
					1 -> Color.BRIGHT
					2 -> Color.MID
					else -> Color.DARK
				},
				filled = false
			)
		}

		// radial streaks
		for (i in 0 until Streak.COUNT) {
			val angleRad = Math.PI * (2.0 * i / Streak.COUNT + if (closing) -progress else progress)

			commands += radialLine(
				angleRad = angleRad.toFloat(),
				innerRadius = radius * Streak.MIN_LENGTH_UNIT,
				outerRadius = radius * (Streak.MAX_LENGTH_UNIT + (i % 3) * Streak.MAX_LENGTH_VARIATION),
				color = if (i % 3 == 0) Color.BRIGHT else Color.MID
			)
		}

		// small bright center
		commands += DrawCommand.Circle(
			cx = 0f,
			cy = 0f,
			radius = radius * Core.RADIUS_UNITS,
			color = Color.CORE,
			filled = true
		)

		return commands
	}


	private fun radialLine(angleRad: Float, innerRadius: Float, outerRadius: Float, color: Int): DrawCommand.Line {
		val cos = cos(angleRad)
		val sin = sin(angleRad)

		return DrawCommand.Line(
			x1 = cos * innerRadius,
			y1 = sin * innerRadius,
			x2 = cos * outerRadius,
			y2 = sin * outerRadius,
			color = color
		)
	}
}

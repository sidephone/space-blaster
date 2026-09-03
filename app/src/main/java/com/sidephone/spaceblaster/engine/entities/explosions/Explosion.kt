package com.sidephone.spaceblaster.engine.entities.explosions

import com.sidephone.spaceblaster.engine.graphics.DrawCommand
import com.sidephone.spaceblaster.engine.graphics.DrawCommandGroup
import kotlin.math.cos
import kotlin.math.log
import kotlin.math.sin

abstract class Explosion(private val startTime: Long, private val position: Pair<Float, Float>) {
	companion object {
		const val COLOR = 0x00FFFF60 // alpha is applied depending on the distance from the center
	}

	private val particles = getParticles()

	abstract fun duration(): Long
	abstract fun getParticles(): List<ExplosionParticle>


	fun spread(now: Long) {
		if (particles.isEmpty()) {
			return
		}

		val elapsedTime = now - startTime
		if (elapsedTime > duration()) {
			return
		}

		for (particle in particles) {
			val distance = particle.speed * elapsedTime / 1000f
			particle.cx = (distance * cos(particle.direction)).toFloat()
			particle.cy = (distance * sin(particle.direction)).toFloat()

		}
	}


	fun draw(now: Long): DrawCommandGroup {
		if (particles.isEmpty()) {
			return DrawCommandGroup(position.first, position.second, 0f, emptyList())
		}

		val elapsedTime = now - startTime
		if (elapsedTime > duration()) {
			return DrawCommandGroup(position.first, position.second, 0f, emptyList())
		}

		val alpha = 255 * (1 - log(elapsedTime.toDouble(), 1000.0) / log(duration().toDouble(), 1000.0))
		val colorWithAlpha = (alpha.toInt() shl 24) or (COLOR and 0x00FFFFFF)

		val drawCommands = particles.map { p ->
			DrawCommand.Circle(
				p.cx,
				p.cy,
				p.size,
				colorWithAlpha,
				true
			)
		}

		return DrawCommandGroup(position.first, position.second, 0f, drawCommands)
	}
}

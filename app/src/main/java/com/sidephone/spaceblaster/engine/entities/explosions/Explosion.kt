package com.sidephone.spaceblaster.engine.entities.explosions

import com.sidephone.spaceblaster.engine.graphics.DrawCommand
import com.sidephone.spaceblaster.engine.graphics.DrawCommandGroup
import kotlin.math.cos
import kotlin.math.log
import kotlin.math.sin

abstract class Explosion(private val startTime: Long, private val position: Pair<Float, Float>) {
	private val particles = getParticles()

	abstract fun color(): Int
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

		val elapsedForAlpha = elapsedTime.coerceAtLeast(1L)
		val fade = 1 - log(elapsedForAlpha.toDouble(), 1000.0) / log(duration().toDouble(), 1000.0)
		val alpha = (255.0 * fade).coerceIn(0.0, 255.0).toInt()
		val colorWithAlpha = (alpha shl 24) or (color() and 0x00FFFFFF)

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

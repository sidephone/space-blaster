package com.sidephone.spaceblaster.engine.entities.explosions

class AsteroidExplosion(startTime: Long, position: Pair<Float, Float>) : Explosion(startTime, position) {
	companion object {
		const val COLOR = 0x009A9A9A // alpha is applied depending on the distance from the center
		const val DURATION = 800L // ms
		const val PARTICLE_COUNT = 35
		const val PARTICLE_SIZE = 10f
		const val LIGHT_PARTICLE_SPEED = 50.0 // px/s
		const val HEAVY_PARTICLE_CHANCE = 5.0 / 7.0
	}

	override fun color() = COLOR
	override fun duration() = DURATION

	override fun getParticles(): List<ExplosionParticle> {
		val particles = mutableListOf<ExplosionParticle>()

		repeat(PARTICLE_COUNT) {
			particles.add(
				ExplosionParticle(
					0f,
					0f,
					Math.random() < HEAVY_PARTICLE_CHANCE,
					PARTICLE_SIZE,
					LIGHT_PARTICLE_SPEED
				)
			)
		}

		return particles
	}
}

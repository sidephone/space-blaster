package com.sidephone.spaceblaster.engine.entities.explosions

class ExplosionTypeShip(startTime: Long, position: Pair<Float, Float>) : Explosion(startTime, position) {
	companion object {
		const val COLOR = 0x00FFFF60 // alpha is applied depending on elapsed time
		const val DURATION = 1200L // ms
		const val PARTICLE_COUNT = 450
		const val PARTICLE_SIZE = 3f
		const val LIGHT_PARTICLE_SPEED = 66.0 // px/s
		const val HEAVY_PARTICLE_CHANCE = 5.0 / 7.0
	}

	override fun color() = COLOR
	override fun duration() = DURATION

	override fun getParticles(): List<ExplosionParticle> {
		val particles = ArrayList<ExplosionParticle>(PARTICLE_COUNT)

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

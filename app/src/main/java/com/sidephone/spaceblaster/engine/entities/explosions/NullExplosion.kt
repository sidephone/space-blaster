package com.sidephone.spaceblaster.engine.entities.explosions

class NullExplosion : Explosion(0, Pair(0f, 0f)) {
	override fun color() = 0
	override fun duration() = 0L
	override fun getParticles() = emptyList<ExplosionParticle>()
}

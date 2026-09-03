package com.sidephone.spaceblaster.engine.entities.explosions

class ExplosionParticle(var cx: Float, var cy: Float, val isHeavy: Boolean, val size: Float, lightParticleSpeed: Double) {
	val direction = Math.random() * 2 * Math.PI // rad
	val speed = ( // px/s
		lightParticleSpeed * if (isHeavy) (0.6 + Math.random() / 7) else Math.random()
	).toFloat()
}

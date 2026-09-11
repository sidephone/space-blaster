package com.sidephone.spaceblaster.engine.entities

data class SpawnPosition(val x: Float, val y: Float, val direction: Float)


/**
 * Returns (x, y, direction) coordinates for the enemy spawn position.
 * The enemy will spawn outside the viewport, either to the left or right, and will move towards the
 * center of the viewport.
 */
fun getEnemySpawnPositions(viewportWidth: Float, viewportHeight: Float, enemyRadius: Float): SpawnPosition {
	var x: Float
	var direction: Float

	x = enemyRadius * (1 + Math.random().toFloat())
	if (Math.random() < 0.5) {
		x += viewportWidth
		direction = 120f + 120f * Math.random().toFloat()
	} else {
		x = -x
		direction = 300f + 120f * Math.random().toFloat()
	}

	return SpawnPosition(
		x,
		viewportHeight * Math.random().toFloat(),
		direction
	)
}

/**
 * Returns (x, y) coordinates for the player spawn position.
 */
fun getPlayerSpawnPosition(viewportWidth: Float, viewportHeight: Float): SpawnPosition {
	return SpawnPosition(viewportWidth / 2f, viewportHeight / 2f, 0f)
}

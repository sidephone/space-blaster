package com.sidephone.spaceblaster.engine.entities

import com.sidephone.spaceblaster.engine.graphics.DrawCommand
import com.sidephone.spaceblaster.engine.graphics.DrawCommandGroup
import com.sidephone.spaceblaster.settings.Settings
import kotlin.math.cos
import kotlin.math.sin


/**
 * An example on how to use the DrawCommand class to draw a spaceship. You can use this as a reference
 * to create your own game objects.
 */
class Ship {
	companion object {
		const val INITIAL_DIRECTION = -90f // degrees, 0 is to the right, -90 is straight up
		const val RADIUS: Float = 30f

		const val MOVE_SPEED = 180f // px per second
		const val TURN_SPEED = 150f // degrees per second
		val MOVE_STEP_MAX: Float = MOVE_SPEED / Settings.TARGET_IPS.toFloat()
		val TURN_STEP_MAX: Float = TURN_SPEED / Settings.TARGET_IPS.toFloat()

		object Cannon {
			const val COLOR: Int = 0xffddecee.toInt()
			const val WIDTH = RADIUS * 0.1f
			const val HEIGHT = RADIUS * 0.8f
		}

		object Fuselage {
			const val COLOR: Int = 0Xffbed9dd.toInt() // navy blue
			const val WIDTH: Float = RADIUS * 0.75f
			const val HEIGHT: Float = RADIUS * 0.75f
		}

		object Wing {
			const val COLOR: Int = 0xffeeeeee.toInt()
			const val WIDTH: Float = RADIUS * 0.5f
			const val HEIGHT: Float = RADIUS * 0.5f
		}
	}

	private var drawCommands: List<DrawCommand> = listOf()

	private var direction: Float = 0f // degrees, 0 is to the right, -90 is straight up
	private var x: Float = 0f // px, center of the ship
	private var y: Float = 0f // px, center of the ship

	private var lastTurnTime = 0L // ms
	private var lastMoveTime = 0L // ms


	/**
	 * Initializes a new ship, so it is ready to be manipulated and drawn on the screen. Use this
	 * at the beginning of the game, or when the ship is destroyed and needs to respawn.
	 */
	fun spawn(viewportWidth: Float, viewportHeight: Float) {
		x = viewportWidth / 2f
		y = viewportHeight / 2f
		direction = INITIAL_DIRECTION
		if (drawCommands.isEmpty()) {
			drawCommands = getDrawCommands()
		}
	}


	/**
	 * Moves the ship one step forward in the direction it is currently facing. The step is calculated
	 * based on the time elapsed since the last move, and is capped at a maximum value to prevent
	 * large jumps. In this example the ship wraps around the screen, but you can make it bounce off
	 * or die.
	 */
	fun moveForward(now: Long, viewportWidth: Float, viewportHeight: Float) {
		val moveSpeed = (MOVE_SPEED * (now - lastMoveTime) / 1000f).coerceAtMost(MOVE_STEP_MAX)
		lastMoveTime = now

		val angle = Math.toRadians(direction.toDouble())
		x += moveSpeed * cos(angle).toFloat()
		y += moveSpeed * sin(angle).toFloat()

		if (x < 0) x = viewportWidth
		if (y < 0) y = viewportHeight
		if (x > viewportWidth) x = 0f
		if (y > viewportHeight) y = 0f
	}


	/**
	 * Turns the ship left or right based on the current direction and the time elapsed since the last
	 * turn. The turn speed is capped at a maximum value to prevent large jumps.
	 */
	fun turn(now: Long, left: Boolean) {
		val turnSpeed = (TURN_SPEED * (now - lastTurnTime) / 1000f).coerceAtMost(TURN_STEP_MAX)
		lastTurnTime = now

		direction += if (left) -turnSpeed else turnSpeed
	}


	/**
	 * Bundles the current position and direction with the list of draw commands in a DrawCommandGroup.
	 * Since the ship is drawn facing upwards, we also permanently rotate it by 90 degrees to appear
	 * correctly on the screen (because the zero direction is to the right).
	 */
	fun draw(): DrawCommandGroup {
		return DrawCommandGroup(x, y, direction + 90f, drawCommands)
	}


	/**
	 * Draw this ship in a local coordinate system, with the origin at the center of the ship. The ship
	 * is drawn facing upwards. The resulting list of draw commands is always the same, so we can cache
	 * it and reuse it for every frame.
	 */
	private fun getDrawCommands(): List<DrawCommand> {
		// Order matters - last objects will be drawn on top of the previous ones.
		return listOf(
			// cannon
			DrawCommand.Rect(
				-Cannon.WIDTH / 2,
				-Cannon.HEIGHT,
				Cannon.WIDTH / 2,
				0f,
				0f,
				Cannon.COLOR,
				filled = true
			),

			// left wing
			DrawCommand.Polygon(
				listOf(
					Pair(Fuselage.WIDTH / 2, Fuselage.HEIGHT / 2),
					Pair(Fuselage.WIDTH / 2 + Wing.WIDTH, Fuselage.HEIGHT / 2),
					Pair(Fuselage.WIDTH / 2, Fuselage.HEIGHT / 2 - Wing.HEIGHT)
				),
				0f,
				Wing.COLOR,
				filled = true
			),

			// right wing
			DrawCommand.Polygon(
				listOf(
					Pair(-Fuselage.WIDTH / 2, Fuselage.HEIGHT / 2),
					Pair(-Fuselage.WIDTH / 2 - Wing.WIDTH, Fuselage.HEIGHT / 2),
					Pair(-Fuselage.WIDTH / 2, Fuselage.HEIGHT / 2 - Wing.HEIGHT)
				),
				0f,
				Wing.COLOR,
				filled = true
			),

			// fuselage
			DrawCommand.Polygon(
				listOf(
					Pair(-Fuselage.WIDTH / 2, -Fuselage.HEIGHT / 2),
					Pair(Fuselage.WIDTH / 2, -Fuselage.HEIGHT / 2),
					Pair(Fuselage.WIDTH / 2, Fuselage.HEIGHT / 2),
					Pair(-Fuselage.WIDTH / 2, Fuselage.HEIGHT / 2)
				),
				0f,
				Fuselage.COLOR,
				filled = true
			),
		)
	}
}

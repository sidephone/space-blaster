package com.sidephone.spaceblaster.engine.entities

import com.sidephone.spaceblaster.engine.graphics.DrawCommand
import com.sidephone.spaceblaster.engine.graphics.DrawCommandGroup
import kotlin.math.max

/**
 * This represents the background of the game. Currently, it is just a solid color, but you can
 * add stars and other elements to make it more interesting. To do this, create a function to
 * generate DrawCommand objects, similar to the Ship class, and add them to the GameFrame in the
 * Gameplay.render()
 */
class Space {
	private data class Star(val x: Float, val y: Float, val size: Float, val brightness: Int)

	companion object {
		const val BACKGROUND = 0xFF000000.toInt()
	}

	object Stars {
		const val BRIGHTNESS_VARIATION = 30 // 0-255
		const val BRIGHTNESS_MIN = 48 // 0-255
		const val BRIGHTNESS_MAX = 120 // 0-255
		const val COLOR = 0x00FFFFFF
		const val MIN_SIZE = 2.4f // px
		const val MAX_SIZE = 2.7f // px
		const val MIN_TWINKLE_TIME = 60 // ms
	}


	private var stars: List<Star> = emptyList()
	private var starDrawCommands: MutableList<DrawCommand> = mutableListOf()
	private var lastStarTwinkle = 0L
	private var starsTwinkled = false


	fun shouldStarsTwinkle(now: Long): Boolean {
		return now - lastStarTwinkle > Stars.MIN_TWINKLE_TIME
	}


	fun bigBang(viewportWidth: Float, viewportHeight: Float) {
		val maxX = viewportWidth.toInt()
		val maxY = viewportHeight.toInt()
		val maxStars = max(viewportWidth, viewportHeight) / 16

		stars = List(maxStars.toInt()) {
			Star(
				x = (0..maxX).random().toFloat(),
				y = (0..maxY).random().toFloat(),
				size = Stars.MIN_SIZE + (Stars.MAX_SIZE - Stars.MIN_SIZE) * Math.random().toFloat(),
				brightness = Stars.BRIGHTNESS_MIN + ((Stars.BRIGHTNESS_MAX - Stars.BRIGHTNESS_MIN) * Math.random()).toInt(),
			)
		}
	}


	fun draw(now: Long): DrawCommandGroup {
		if (shouldStarsTwinkle(now)) {
			starDrawCommands = drawStars().toMutableList()
			lastStarTwinkle = now
			starsTwinkled = true
		} else {
			starsTwinkled = false
		}

		return DrawCommandGroup(0f, 0f, 0f, starDrawCommands)
	}


	fun drawStars(): List<DrawCommand> {
		val drawCommands = mutableListOf<DrawCommand>()

		for (star in stars) {
			// add twinkling effect
			val brightnessVariation = (0..Stars.BRIGHTNESS_VARIATION).random()
			val color = Stars.COLOR or ((star.brightness - brightnessVariation).coerceAtLeast(Stars.BRIGHTNESS_MIN) shl 24)

			drawCommands.add(
				DrawCommand.Circle(
					star.x,
					star.y,
					star.size,
					color = color,
					true
				)
			)
		}
		return drawCommands
	}
}

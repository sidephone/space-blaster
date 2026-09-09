package com.sidephone.spaceblaster.engine.entities

import android.graphics.Color
import com.sidephone.spaceblaster.engine.entities.ships.PlayerShip
import com.sidephone.spaceblaster.engine.graphics.DrawCommand
import com.sidephone.spaceblaster.engine.graphics.DrawCommandGroup
import kotlin.math.ceil

class Hud {
	object Countdown {
		const val COLOR = Color.WHITE
		const val SIZE = 45f
	}

	object Icon {
		const val X = 32f
		const val Y_BOTTOM = 30f
		const val SCALE = 0.6f
	}

	object Lives {
		const val COLOR = Color.WHITE
		const val X = 58f
		const val Y_BOTTOM = 20f
		const val SIZE = 30f
	}

	private var icon: DrawCommandGroup? = null
	private var iconViewportHeight: Float? = null

	fun draw(now: Long, viewportWidth: Float, viewportHeight: Float, player: PlayerShip, countDownEnd: Long): List<DrawCommandGroup> {
		if (icon == null || iconViewportHeight != viewportHeight) {
			icon = player.draw(Icon.X, viewportHeight - Icon.Y_BOTTOM, Icon.SCALE)
			iconViewportHeight = viewportHeight
		}

		val output = mutableListOf<DrawCommandGroup>()
		icon?.let { output.add(it) }
		output.add(printLives(viewportHeight, player))
		printCountdown(now, viewportWidth, viewportHeight, countDownEnd)?.let { output.add(it) }
		return output
	}


	private fun printCountdown(now: Long, viewportWidth: Float, viewportHeight: Float, countDownEnd: Long): DrawCommandGroup? {
		val remainingTime = ceil((countDownEnd - now) / 1000.0).toInt()
		return if (remainingTime > 0) {
			DrawCommandGroup(
				viewportWidth / 2f,
				viewportHeight / 2f,
				listOf(DrawCommand.Text("$remainingTime", 0f, 0f, Countdown.SIZE, Countdown.COLOR))
			)
		} else {
			null
		}
	}


	private fun printLives(viewportHeight: Float, player: PlayerShip): DrawCommandGroup {
		return DrawCommandGroup(
			Lives.X,
			viewportHeight - Lives.Y_BOTTOM,
			listOf(DrawCommand.Text("x ${player.lives.value}", 0f, 0f, Lives.SIZE, Lives.COLOR))
		)
	}
}

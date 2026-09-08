package com.sidephone.spaceblaster.engine.entities

import android.graphics.Color
import com.sidephone.spaceblaster.engine.entities.ships.PlayerShip
import com.sidephone.spaceblaster.engine.graphics.DrawCommand
import com.sidephone.spaceblaster.engine.graphics.DrawCommandGroup

class Hud {
	object Icon {
		const val X = 32f
		const val Y_BOTTOM = 30f
		const val SCALE = 0.6f
	}

	object Text {
		const val COLOR = Color.WHITE
		const val X = 58f
		const val Y_BOTTOM = 20f
		const val SIZE = 30f
	}

	var icon: DrawCommandGroup? = null

	private var icon: DrawCommandGroup? = null
	private var iconViewportHeight: Float? = null

	fun draw(viewportHeight: Float, player: PlayerShip): List<DrawCommandGroup> {
		if (icon == null || iconViewportHeight != viewportHeight) {
			icon = player.draw(Icon.X, viewportHeight - Icon.Y_BOTTOM, Icon.SCALE)
			iconViewportHeight = viewportHeight
		}

		val output = mutableListOf<DrawCommandGroup>()
		icon?.let { output.add(it) }
		output.addAll(drawText(viewportHeight, player))
		return output
	}


	private fun drawText(viewportHeight: Float, player: PlayerShip): List<DrawCommandGroup> {
		val output = mutableListOf<DrawCommandGroup>()
		output.add(DrawCommandGroup(
			Text.X,
			viewportHeight - Text.Y_BOTTOM,
			listOf(DrawCommand.Text("x ${player.lives.value}", 0f, 0f, Text.SIZE, Text.COLOR))
		))
		return output
	}
}

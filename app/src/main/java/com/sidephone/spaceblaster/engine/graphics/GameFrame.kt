package com.sidephone.spaceblaster.engine.graphics

import android.graphics.Color

data class GameFrame(
	val backgroundColor: Int = Color.BLACK,
	val commandGroups: List<DrawCommandGroup> = emptyList()
)

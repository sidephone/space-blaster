package com.sidephone.spaceblaster.screens.game

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.sidephone.spaceblaster.engine.Gameplay
import com.sidephone.spaceblaster.ui.theme.Dimens

@Composable
fun HudOverlay(textColor: Color, highScore: Int, gameplay: Gameplay) {
	val score by gameplay.score.collectAsState()

	Row(modifier = Modifier.padding(Dimens.HudPadding)) {
		Text(
			text = "Score: $score・High: $highScore",
			style = typography.bodyLarge,
			color = textColor,
			textAlign = TextAlign.Center,
			modifier = Modifier.fillMaxWidth()
		)
	}
}

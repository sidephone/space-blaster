package com.sidephone.spaceblaster.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sidephone.spaceblaster.R
import com.sidephone.spaceblaster.ui.theme.Dimens

@Composable
fun GameOverOverlay(textColor: Color, backgroundColor: Color) {
	Column(
		modifier = Modifier
			.background(backgroundColor)
			.fillMaxSize()
			.padding(16.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Box(
			modifier = Modifier.fillMaxSize()
		) {
			Column(
				modifier = Modifier.align(Alignment.Center).fillMaxWidth()
			) {
				Text(
					text = stringResource(R.string.game_over),
					color = textColor,
					modifier = Modifier.padding(bottom = Dimens.MainMenuTitlePaddingBottom).fillMaxWidth(),
					fontSize = MaterialTheme.typography.headlineLarge.fontSize,
					textAlign = TextAlign.Center
				)

				Text(
					text = stringResource(R.string.game_over_subtitle),
					color = textColor,
					modifier = Modifier.fillMaxWidth(),
					fontSize = MaterialTheme.typography.bodyLarge.fontSize,
					textAlign = TextAlign.Center
				)
			}
		}
	}
}

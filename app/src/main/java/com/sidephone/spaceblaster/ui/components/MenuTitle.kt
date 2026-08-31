package com.sidephone.snake.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.sidephone.spaceblaster.ui.theme.Dimens

@Composable
fun MenuTitle(text: String) {
	Text(
		text = text,
		style = MaterialTheme.typography.headlineLarge,
		textAlign = TextAlign.Center,
		modifier = Modifier.padding(
			top = Dimens.MainMenuTitlePaddingTop,
			bottom = Dimens.MainMenuTitlePaddingBottom
		),
		color = MaterialTheme.colorScheme.onBackground
	)
}

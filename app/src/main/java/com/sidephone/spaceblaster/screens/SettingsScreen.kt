package com.sidephone.spaceblaster.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sidephone.spaceblaster.R
import com.sidephone.spaceblaster.ui.components.MenuButton
import com.sidephone.spaceblaster.ui.modifiers.gamepadClickableButton
import com.sidephone.spaceblaster.ui.theme.Dimens
import com.sidephone.snake.ui.components.MenuTitle

@Composable
fun SettingsScreen(onBack: () -> Unit) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
			.padding(Dimens.MainMenuButtonContainerPadding),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Top
	) {
		MenuTitle(text = stringResource(R.string.main_settings))

		Text(
			text = "No settings available yet.",
			style = MaterialTheme.typography.bodyLarge,
			color = MaterialTheme.colorScheme.onBackground,
			modifier = Modifier.padding(bottom = Dimens.MainMenuButtonPaddingBottom)
		)

		MenuButton(
			onClick = onBack,
			modifier = Modifier.gamepadClickableButton(onBack),
			text = R.string.main_back
		)
	}
}

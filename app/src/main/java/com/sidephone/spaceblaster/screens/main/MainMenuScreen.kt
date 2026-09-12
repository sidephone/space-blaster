package com.sidephone.spaceblaster.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import com.sidephone.spaceblaster.R
import com.sidephone.spaceblaster.ui.components.MenuButton
import com.sidephone.spaceblaster.ui.modifiers.gamepadClickableButton
import com.sidephone.spaceblaster.ui.theme.Dimens

@Composable
fun MainMenuScreen(
	isGamePaused: Boolean = false,
	onNewGame: () -> Unit,
	onEndGame: () -> Unit,
	onControls : () -> Unit,
	onSettings: () -> Unit,
	onExit: () -> Unit
) {
	val firstButtonFocusRequester = remember { FocusRequester() }

	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
			.padding(Dimens.MainMenuButtonContainerPadding),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Top
	) {
		val hasRequestedInitialFocus = remember { mutableStateOf(false) }

		Logo()

		MenuButton(
			onClick = onNewGame,
			text = if (isGamePaused) R.string.main_resume_game else R.string.main_new_game,
			modifier = Modifier
				.gamepadClickableButton(onNewGame)
				.focusRequester(firstButtonFocusRequester)
				.onGloballyPositioned {
					if (!hasRequestedInitialFocus.value) {
						hasRequestedInitialFocus.value = true
						firstButtonFocusRequester.requestFocus()
					}
				}
		)

		if (isGamePaused) {
			MenuButton(
				onClick = onEndGame,
				modifier = Modifier.gamepadClickableButton(onEndGame),
				text = R.string.main_end_game
			)
		}

		MenuButton(
			onClick = onControls,
			modifier = Modifier.gamepadClickableButton(onControls),
			text = R.string.main_controls
		)

		if (!isGamePaused) {
			MenuButton(
				onClick = onSettings,
				modifier = Modifier.gamepadClickableButton(onSettings),
				text = R.string.main_settings
			)
		}

		MenuButton(
			onClick = onExit,
			modifier = Modifier.gamepadClickableButton(onExit),
			text = R.string.main_exit
		)
	}
}

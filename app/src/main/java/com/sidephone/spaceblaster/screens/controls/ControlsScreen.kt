package com.sidephone.spaceblaster.screens.controls
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sidephone.spaceblaster.R
import com.sidephone.spaceblaster.ui.components.BackToMainButton
import com.sidephone.spaceblaster.ui.components.MenuTitle
import com.sidephone.spaceblaster.ui.theme.Dimens


@Composable
fun ControlsScreen(onBack: () -> Unit) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
			.padding(Dimens.MainMenuButtonContainerPadding),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Top
	) {
		MenuTitle(text = stringResource(R.string.main_controls))

		Column(
			modifier = Modifier.fillMaxSize(),
			horizontalAlignment = Alignment.Start,
		) {
			KeypadName(R.string.controls_gamepad_title)
			KeypadControlsList(R.string.controls_gamepad_list)

			KeypadName(R.string.controls_numpad_title)
			KeypadControlsList(R.string.controls_numpad_list)

			KeypadName(R.string.controls_qwerty_title)
			KeypadControlsList(R.string.controls_qwerty_list)
		}

		BackToMainButton(onBack = onBack)
	}
}

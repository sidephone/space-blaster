package com.sidephone.spaceblaster.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sidephone.snake.ui.components.MenuTitle
import com.sidephone.spaceblaster.R
import com.sidephone.spaceblaster.settings.Settings
import com.sidephone.spaceblaster.ui.components.BackToMainButton
import com.sidephone.spaceblaster.ui.components.SettingsSwitch
import com.sidephone.spaceblaster.ui.theme.Dimens

@Composable
fun SettingsScreen(settings: Settings, onBack: () -> Unit) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
			.padding(Dimens.MainMenuButtonContainerPadding),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Top
	) {
		MenuTitle(text = stringResource(R.string.main_settings))

		var enemyCrashDisabled by remember { mutableStateOf(!settings.getEnemiesAllowed()) }

		SettingsSwitch(
			titleResId = R.string.setting_asteroids_bump,
			summaryOnResId = R.string.setting_asteroids_bump_on,
			summaryOffResId = R.string.setting_asteroids_bump_off,
			value = settings.getAsteroidsBump(),
			false,
			onValueChange = { settings.setAsteroidsBump(it) }
		)

		SettingsSwitch(
			titleResId = R.string.setting_bullets_wrap,
			summaryOnResId = R.string.setting_bullets_wrap_on,
			summaryOffResId = R.string.setting_bullets_wrap_off,
			value = settings.getBulletsWrapAround(),
			disabled = false,
			onValueChange = { settings.setBulletsWrapAround(it) }
		)

		SettingsSwitch(
			titleResId = R.string.setting_enemies_enabled,
			summaryOnResId = R.string.setting_enemies_enabled_on,
			summaryOffResId = R.string.setting_enemies_enabled_off,
			value = settings.getEnemiesAllowed(),
			disabled = false,
			onValueChange = {
				settings.setEnemiesAllowed(it)
				enemyCrashDisabled = !it
			}
		)

		SettingsSwitch(
			titleResId = R.string.setting_enemies_crash_in_asteroids,
			summaryOnResId = R.string.setting_enemies_crash_in_asteroids_on,
			summaryOffResId = R.string.setting_enemies_crash_in_asteroids_off,
			value = settings.getEnemiesCrashInAsteroids(),
			disabled = enemyCrashDisabled,
			onValueChange = { settings.setEnemiesCrashInAsteroids(it) }
		)

		BackToMainButton(onBack = onBack)
	}
}

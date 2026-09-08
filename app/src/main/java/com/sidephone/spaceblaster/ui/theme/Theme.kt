package com.sidephone.spaceblaster.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable


@Composable
fun GameTheme(
	darkTheme: Boolean = isSystemInDarkTheme(),
	// dynamic colors seem to be causing discrepancies on our devices, so we stick with a fixed
	// color scheme for now
	dynamicColor: Boolean = false,
	content: @Composable () -> Unit
) {
	MaterialTheme(
		colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
		typography = Typography,
		content = content
	)
}

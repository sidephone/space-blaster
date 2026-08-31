package com.sidephone.spaceblaster.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sidephone.spaceblaster.ui.theme.Dimens

/**
 * The default Material3 Button highlighting when focused is too subtle. This defines more obvious
 * colors for the focused state.
 */
@Composable
fun MenuButton(
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	text: Int
) {
	val interactionSource = remember { MutableInteractionSource() }
	val isFocused by interactionSource.collectIsFocusedAsState()

	Button(
		onClick = onClick,
		interactionSource = interactionSource,
		colors = ButtonDefaults.buttonColors(
			// background color
			containerColor = if (isFocused) MaterialTheme.colorScheme.secondary
			else MaterialTheme.colorScheme.background,

			// text color
			contentColor = if (isFocused) MaterialTheme.colorScheme.onSecondary
			else MaterialTheme.colorScheme.onBackground
		),
		modifier = modifier
			.fillMaxWidth()
			.padding(
				bottom = Dimens.MainMenuButtonPaddingBottom,
				start = Dimens.MainMenuButtonPaddingHorizontal,
				end = Dimens.MainMenuButtonPaddingHorizontal
			),
	) {
		Text(
			text = stringResource(text),
			style = MaterialTheme.typography.titleLarge,
		)
	}
}

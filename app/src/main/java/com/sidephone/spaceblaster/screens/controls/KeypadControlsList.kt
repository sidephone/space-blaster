package com.sidephone.spaceblaster.screens.controls

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sidephone.spaceblaster.ui.theme.Dimens

@Composable
fun KeypadControlsList(textResId: Int) {
	Text(
		style = MaterialTheme.typography.bodyLarge,
		modifier = Modifier.padding(
			top = 0.dp,
			start = Dimens.ControlsListPaddingHorizontal,
			end = Dimens.ControlsListPaddingHorizontal,
			bottom = Dimens.ControlsListPaddingBottom
		),
		color = MaterialTheme.colorScheme.onBackground,
		text = stringResource(textResId)
	)
}

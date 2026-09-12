package com.sidephone.spaceblaster.screens.controls

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.sidephone.spaceblaster.ui.theme.Dimens

@Composable
fun KeypadName(textResId: Int) {
	Text(
		style = MaterialTheme.typography.headlineSmall,
		fontWeight = FontWeight.Bold,
		modifier = Modifier.padding(bottom = Dimens.ControlsKeypadTitlePaddingBottom),
		color = MaterialTheme.colorScheme.onBackground,
		text = stringResource(textResId)
	)
}

package com.sidephone.spaceblaster.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sidephone.spaceblaster.R
import com.sidephone.spaceblaster.ui.theme.Dimens

@Composable
fun Logo() {
	Image(
		contentDescription = stringResource(id = R.string.app_name),
		painter = painterResource(id = R.drawable.logo),
		modifier = Modifier.padding(
			start = Dimens.MainMenuTitlePaddingHorizontal,
			end = Dimens.MainMenuTitlePaddingHorizontal,
			bottom = Dimens.MainMenuTitlePaddingBottom
		),
	)
}

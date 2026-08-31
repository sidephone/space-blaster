package com.sidephone.spaceblaster.ui.modifiers

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import android.view.KeyEvent as AndroidKeyEvent

/**
 * By default, Android Buttons can be selected with the D-Pad, but can only be clicked with Enter,
 * or D-pad Center. This modifier enables clicking with the Start button, or A/B buttons on a gamepad,
 * for user convenience.
 */
fun Modifier.gamepadClickableButton(onClick: () -> Unit): Modifier =
	this.onKeyEvent { event: KeyEvent ->
		if (
			event.type == KeyEventType.KeyDown
			&& (
				// add or remove key codes here, to define which buttons can trigger the click event
				event.nativeKeyEvent.keyCode == AndroidKeyEvent.KEYCODE_BUTTON_START
				|| event.nativeKeyEvent.keyCode == AndroidKeyEvent.KEYCODE_DPAD_CENTER
				|| event.nativeKeyEvent.keyCode == AndroidKeyEvent.KEYCODE_BUTTON_A
			)
		) {
			onClick()
			true // consume the event
		} else {
			false
		}
	}

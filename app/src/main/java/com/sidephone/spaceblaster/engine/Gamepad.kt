package com.sidephone.spaceblaster.engine

import android.view.KeyEvent


/**
 * Represents the gamepad controller. It takes input from any Activity.onKeyDown, Activity.onKeyUp,
 * then stores the pressed keys in a set, to be used by other game components. The keys are represented
 * by their KeyEvent key codes, and remain in the set for as long as the user holds the button down.
 * When the button is released, the key is removed from the set.
 * This class already supports all gamepad buttons, so you should not need to modify it.
 */
class Gamepad {
	val pressedKeys = mutableSetOf<Int>()


	fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
		if (hasKey(keyCode) && event?.repeatCount == 0) {
			pressedKeys.add(normalizeKeyCode(keyCode))
			return true
		}

		return false
	}


	fun onKeyUp(keyCode: Int): Boolean {
		if (hasKey(keyCode)) {
			pressedKeys.remove(normalizeKeyCode(keyCode))
			return true
		}

		return false
	}


	fun reset() {
		pressedKeys.clear()
	}


	private fun hasKey(keyCode: Int): Boolean {
		return when (keyCode) {
			// QWERTY
			KeyEvent.KEYCODE_SPACE,
			KeyEvent.KEYCODE_DEL,
			// gamepad
			KeyEvent.KEYCODE_BUTTON_A,
			KeyEvent.KEYCODE_BUTTON_B,
			KeyEvent.KEYCODE_BUTTON_X,
			KeyEvent.KEYCODE_BUTTON_Y,
			KeyEvent.KEYCODE_BUTTON_SELECT,
			KeyEvent.KEYCODE_BUTTON_START,
			KeyEvent.KEYCODE_DPAD_UP,
			KeyEvent.KEYCODE_DPAD_DOWN,
			KeyEvent.KEYCODE_DPAD_LEFT,
			KeyEvent.KEYCODE_DPAD_RIGHT -> true
			else -> false
		}
	}


	private fun normalizeKeyCode(keyCode: Int): Int {
		return when (keyCode) {
			KeyEvent.KEYCODE_SPACE -> KeyEvent.KEYCODE_BUTTON_A
			KeyEvent.KEYCODE_DEL -> KeyEvent.KEYCODE_BUTTON_Y
			else -> keyCode
		}
	}
}

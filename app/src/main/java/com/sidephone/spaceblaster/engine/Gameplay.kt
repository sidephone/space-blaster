package com.sidephone.spaceblaster.engine

import android.util.Log
import android.view.KeyEvent
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.sidephone.spaceblaster.engine.entities.AsteroidList
import com.sidephone.spaceblaster.engine.entities.Ship
import com.sidephone.spaceblaster.engine.entities.Space
import com.sidephone.spaceblaster.engine.graphics.DrawCommandGroup
import com.sidephone.spaceblaster.engine.graphics.GameFrame
import com.sidephone.spaceblaster.settings.Settings
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit


/**
 * The main game engine class. It contains the game loop, input handling, and game state management.
 * It is designed to be simple and easy to understand, so you can modify it to create your own game.
 */
class Gameplay {
	companion object {
		private val LOG_TAG = Gameplay::class.java.simpleName
	}

	// game loop
	private var executor = Executors.newSingleThreadScheduledExecutor()
	private var engineLooper: Future<*>? = null
	private var isPaused = false

	// input
	@Volatile private var pressedKeys = setOf<Int>()

	// output
	private var onStartButtonPressed = {}
	private var onStarted = {}

	// graphics
	@Volatile private var viewportWidth = 1f
	@Volatile private var viewportHeight = 1f
	@Volatile var currentFrame: GameFrame = GameFrame()

	// game objects
	private val player = Ship()
	private val space = Space()
	private var asteroids = AsteroidList()

	// game state
	private var stage = 1


	/**
	 * Set the initial state of the game. Call this whenever you need to restart the game.
	 */
	@MainThread
	fun reset() {
		pressedKeys = setOf()

		stage = 1

		space.bigBang(viewportWidth, viewportHeight)
		player.spawn(viewportWidth, viewportHeight)
		asteroids.spawn(stage, player, viewportWidth, viewportHeight)

		if (!isGameThreadAlive()) {
			if (!executor.isShutdown && !executor.isTerminated) {
				executor.shutdownNow()
			}
			executor = Executors.newSingleThreadScheduledExecutor()
		}
	}


	/**
	 * Handle the pressed keys for your game logic.
	 * For each key you can call appropriate handler. E.g. if KeyEvent.KEYCODE_DPAD_UP, call
	 * "moveUp()" function, or if KeyEvent.KEYCODE_BUTTON_A, call "jump()" function. You can also
	 * choose to ignore some keys if you don't need them for your game.
	 * When a key is released, you will receive a new list of pressed keys without that key.
	 *
	 * @param keys The set of currently pressed keys represented by their KeyEvent key codes.
	 */
	@MainThread
	fun onPressedKeys(keys: Set<Int>) {
		pressedKeys = keys.toSet() // make a copy for thread safety
		preprocessInput()
	}


	/**
	 * Adjust the dimension of the game scene. All rendering will be performed using these.
	 */
	@AnyThread
	fun setViewportSize(width: Int, height: Int) {
		if (width <= 0 || height <= 0) {
			Log.w(LOG_TAG, "Ignoring invalid viewport size: width=$width, height=$height. Must be positive.")
			return
		}

		viewportWidth = width.toFloat()
		viewportHeight = height.toFloat()
	}


	/**
	 * Start or resume the game loop, or if already running, do nothing.
	 */
	@MainThread
	fun start() {
		if (isGameThreadAlive()) {
			return
		}

		isPaused = false
		pressedKeys = emptySet()

		engineLooper = executor.scheduleWithFixedDelay(
			{ advance() },
			0,
			1_000_000_000L / Settings.Gameplay.TARGET_IPS,
			TimeUnit.NANOSECONDS
		)

		onStarted()

		Log.d(LOG_TAG, "Gameplay loop started at ${Settings.Gameplay.TARGET_IPS} iterations per second")
	}


	/**
	 * Pause the game loop, or if already paused, do nothing.
	 */
	@MainThread
	fun pause() {
		if (isPaused) {
			return
		}

		engineLooper?.cancel(true)
		isPaused = true

		Log.d(LOG_TAG, "Gameplay loop paused")
	}


	/**
	 * Stop the game loop and release resources. After calling this, you can not resume the game
	 * anymore, you can only use "reset()" to start a new game.
	 */
	@MainThread
	fun stop() {
		isPaused = false
		executor.shutdownNow()
		Log.d(LOG_TAG, "Gameplay loop stopped")
	}


	/**
	 * A utility function that returns true if the game loop is currently running.
	 */
	@MainThread
	fun isRunning(): Boolean {
		return !isPaused && isGameThreadAlive()
	}


	/**
	 * A utility function that returns true if the game loop is currently paused.
	 */
	@MainThread
	fun isPaused(): Boolean {
		return isPaused
	}


	/**
	 * Handle the "Start" button press. Pauses the game (if running) and notifies listeners (e.g. UI)
	 * so they can navigate back to the main menu or perform other actions.
	 */
	@MainThread
	fun onStartButton() {
		pause()
		onStartButtonPressed()
	}


	/**
	 * Set an optional callback to be invoked when the game is paused. This can be used to navigate
	 * back to the main menu or perform other actions.
	 */
	@MainThread
	fun setOnStartButtonPressedCallback(callback: () -> Unit): Gameplay {
		onStartButtonPressed = callback
		return this
	}


	/**
	 * Set an optional callback to be invoked immediately before the game starts.
	 */
	@MainThread
	fun setOnStartedCallback(callback: () -> Unit): Gameplay {
		onStarted = callback
		return this
	}


	/**
	 * Returns true when the game thread executor is still working.
	 */
	@MainThread
	private fun isGameThreadAlive(): Boolean {
		return !executor.isShutdown && !executor.isTerminated && (engineLooper?.isDone == false)
	}


	/**
	 * The main game loop function. This is equivalent to a single step or "frame" in the game. It
	 * is called repeatedly at a fixed interval to read the input, update state and perform other game
	 * logic. Finally, the "render()" method draws the current state to the screen.
	 */
	@WorkerThread
	private fun advance() {
		try {
			val now = System.currentTimeMillis()
			processGameInput(now)
			render(now)
		} catch (e: Exception) {
			Log.e(LOG_TAG, "Failed advancing ahead gameplay. ${e.message}", e)
		}
	}


	/**
	 * Perform any non-game related actions, immediately after receiving the pressed keys. For example,
	 * pause the game, when "KeyEvent.KEYCODE_BUTTON_START" is pressed.
	 */
	@MainThread
	private fun preprocessInput() {
		if (KeyEvent.KEYCODE_BUTTON_START in pressedKeys) {
			onStartButton()
		}
	}


	/**
	 * Perform various actions, or set state based on the currently pressed keys. This is the first
	 * step in the game loop. All following steps will use the state to calculate actions or draw
	 * objects on the screen.
	 */
	@WorkerThread
	private fun processGameInput(now: Long) {
		val keys = pressedKeys.toSet() // make a copy for thread safety

		val leftPressed = KeyEvent.KEYCODE_DPAD_LEFT in keys
		val rightPressed = KeyEvent.KEYCODE_DPAD_RIGHT in keys
		if (leftPressed xor rightPressed) {
			player.turn(now, left = leftPressed)
		}

		if (KeyEvent.KEYCODE_BUTTON_Y in keys || KeyEvent.KEYCODE_DPAD_DOWN in keys) {
			player.stop(now)
		} else {
			player.thrust(now, KeyEvent.KEYCODE_BUTTON_B in keys || KeyEvent.KEYCODE_DPAD_UP in keys)
		}
	}


	/**
	 * This is the main method that draws to the screen. In this demo, we draw a spaceship that can
	 * move around the screen. The spaceship's position and direction are updated based on the pressed
	 * keys.
	 */
	@WorkerThread
	private fun render(now: Long) {
		player.move(now, viewportWidth, viewportHeight)
		asteroids.move(now, viewportWidth, viewportHeight)

		val screenObjects = mutableListOf<DrawCommandGroup>()
		screenObjects.add(space.draw(now))
		screenObjects.add(player.draw(now))
		screenObjects.addAll(asteroids.draw(now))

		currentFrame = GameFrame(Space.BACKGROUND, screenObjects)
	}
}

package com.sidephone.spaceblaster.screens.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.graphics.withMatrix
import com.sidephone.spaceblaster.engine.Gameplay
import com.sidephone.spaceblaster.engine.graphics.DrawCommand
import com.sidephone.spaceblaster.engine.graphics.GameFrame
import com.sidephone.spaceblaster.settings.Settings
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * A Canvas wrapper that accepts a list of draw commands from the Gameplay engine and renders it to
 * the screen.Runs on a separate thread to avoid blocking other game logic.
 */
class GameSurfaceView(context: Context, private var gameplay: Gameplay, private val menuBackground: Int) : SurfaceView(context), SurfaceHolder.Callback {
	companion object {
		private val LOG_TAG = GameSurfaceView::class.java.simpleName
	}

	private var executor = Executors.newSingleThreadScheduledExecutor()
	private var renderFuture: ScheduledFuture<*>? = null
	private val paint = Paint()
	private var isCanvasCleared = false


	init {
		holder.addCallback(this)
	}


	override fun surfaceCreated(holder: SurfaceHolder) {
		isCanvasCleared = false
		run(holder) // run at least once to hide the Canvas on the first start
		gameplay.setOnStartedCallback { run(holder) }
	}


	override fun surfaceDestroyed(holder: SurfaceHolder) {
		stop()
	}


	override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
		gameplay.setViewportSize(width, height)
	}


	/**
	 * Starts the rendering loop on a separate thread. The loop runs at a fixed rate defined by the
	 * TARGET_FPS constant. Does nothing if the loop is already running.
	 */
	private fun run(holder: SurfaceHolder) {
		if (renderFuture != null) {
			return
		}

		val exec = Executors.newSingleThreadScheduledExecutor()
		executor = exec
		renderFuture = exec.scheduleWithFixedDelay(
			{ render(holder) }, 0, 1_000_000_000L / Settings.Gameplay.TARGET_FPS, TimeUnit.NANOSECONDS
		)

		Log.d(LOG_TAG, "Rendering loop started at ${Settings.Gameplay.TARGET_FPS} FPS")
	}


	/**
	 * Stops the rendering loop and shuts down the executor. Does nothing if the loop is not running.
	 */
	private fun stop() {
		if (renderFuture == null) {
			return
		}

		renderFuture?.cancel(false)
		renderFuture = null
		executor.shutdown()
		Log.d(LOG_TAG, "Rendering loop stopped")
	}


	/**
	 * The main function that accepts and validates the rendering commands, and renders them to the canvas,
	 * and clears the canvas when the game is paused or stopped.
	 */
	private fun render(holder: SurfaceHolder) {
		if (!holder.surface.isValid) {
			return
		}

		var drawCommands: GameFrame?

		if (gameplay.isRunning()) {
			// when running, accept the draw command list from the gameplay engine
			drawCommands = gameplay.currentFrame
			isCanvasCleared = false
		} else if (!isCanvasCleared) {
			// when paused or stopped, run once to clear the canvas with the menu background color
			drawCommands = GameFrame(backgroundColor = menuBackground)
			isCanvasCleared = true
		} else {
			// after clearing the canvas above, stop looping and drawing to save CPU cycles and battery
			stop()
			return
		}

		val canvas = holder.lockCanvas() ?: return
		try {
			drawToCanvas(canvas, drawCommands)
		} finally {
			holder.unlockCanvasAndPost(canvas)
		}
	}


	/**
	 * Creates a rotation matrix to draw a rotated shape around the given point (pivotX, pivotY) by
	 * the given angle in degrees.
	 */
	private fun makeRotationMatrix(angle: Float, pivotX: Float, pivotY: Float): Matrix {
		val matrix = Matrix()
		matrix.postRotate(angle, pivotX, pivotY)
		return matrix
	}


	/**
	 * Draws the provided command list to the given Canvas.
	 */
	private fun drawToCanvas(canvas: Canvas, frame: GameFrame?) {
		if (frame == null) return

		canvas.drawColor(frame.backgroundColor)

		for (commandGroup in frame.commandGroups) {
			if (commandGroup.commands.isEmpty()) continue

			val scale = commandGroup.scale
			val matrix = Matrix()
			matrix.postTranslate(commandGroup.x, commandGroup.y)
			if (scale != 1f) {
				matrix.postScale(scale, scale)
			}

			canvas.withMatrix(matrix) {
				rotate(commandGroup.rotationDegrees)

				for (command in commandGroup.commands) {
					when (command) {
						is DrawCommand.Arc -> drawArc(this, command)
						is DrawCommand.Circle -> drawCircle(this, command)
						is DrawCommand.Dot -> drawDot(this, command)
						is DrawCommand.Line -> drawLine(this, command)
						is DrawCommand.Polygon -> drawPolygon(this, command)
						is DrawCommand.Rect -> drawRectangle(this, command)
					}
				}
			}
		}
	}


	private fun drawArc(canvas: Canvas, command: DrawCommand.Arc) {
		paint.color = command.color
		paint.style = if (command.filled) Paint.Style.FILL else Paint.Style.STROKE
		val rectF = android.graphics.RectF(
			command.cx - command.radius,
			command.cy - command.radius,
			command.cx + command.radius,
			command.cy + command.radius
		)
		canvas.drawArc(rectF, command.startAngle, command.sweepAngle, command.filled, paint)
	}


	private fun drawCircle(canvas: Canvas, command: DrawCommand.Circle) {
		paint.color = command.color
		paint.style = if (command.filled) Paint.Style.FILL else Paint.Style.STROKE
		canvas.drawCircle(command.cx, command.cy, command.radius, paint)
	}


	private fun drawDot(canvas: Canvas, command: DrawCommand.Dot) {
		paint.color = command.color
		canvas.drawPoint(command.x, command.y, paint)
	}


	private fun drawLine(canvas: Canvas, command: DrawCommand.Line) {
		paint.color = command.color
		canvas.drawLine(command.x1, command.y1, command.x2, command.y2, paint)
	}


	private fun drawPolygon(canvas: Canvas, command: DrawCommand.Polygon) {
		if (command.points.isEmpty()) return

		paint.color = command.color
		paint.style = if (command.filled) Paint.Style.FILL else Paint.Style.STROKE

		val path = android.graphics.Path()
		for ((index, point) in command.points.withIndex()) {
			val (x, y) = point
			if (index == 0) {
				path.moveTo(x, y)
			} else {
				path.lineTo(x, y)
			}
		}
		path.close()

		if (command.rotateDeg != 0f) {
			val pivotX = command.points.sumOf { it.first.toDouble() }.toFloat() / command.points.size
			val pivotY = command.points.sumOf { it.second.toDouble() }.toFloat() / command.points.size
			path.transform(makeRotationMatrix(command.rotateDeg, pivotX, pivotY))
		}

		canvas.drawPath(path, paint)
	}


	private fun drawRectangle(canvas: Canvas, command: DrawCommand.Rect) {
		paint.color = command.color
		paint.style = if (command.filled) Paint.Style.FILL else Paint.Style.STROKE

		if (command.rotateDeg == 0f) {
			canvas.drawRect(command.left, command.top, command.right, command.bottom, paint)
			return
		}

		val matrix = makeRotationMatrix(
			command.rotateDeg,
			(command.left + command.right) / 2,
			(command.top + command.bottom) / 2
		)
		canvas.withMatrix(matrix) {
			canvas.drawRect(command.left, command.top, command.right, command.bottom, paint)
		}
	}
}

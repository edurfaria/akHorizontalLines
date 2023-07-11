package com.example.akhorizontallines

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Button
import com.example.akhorizontallines.databinding.ActivityMainBinding
import java.lang.Float.max
import java.lang.Float.min

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var duration: Long = 1000

    private var workingY = false
    private var workingX = false
    private var translateY: Float = 0.0F
    private var translateX: Float = 0.0F

    private lateinit var animateX: ValueAnimator
    private lateinit var animateY: ValueAnimator

    data class RectangleData(
        val isRectangleVisible: Boolean,
        val rectangleLeft: Float,
        val rectangleTop: Float,
        val rectangleRight: Float,
        val rectangleBottom: Float
    )

    //Vars for Rectangle
    private var isRectangleVisible = false

    private var rectangleLeft = 0F
    private var rectangleTop = 0F
    private var rectangleRight = 0F
    private var rectangleBottom = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)
        setupHorizontalLines()
    }

    /**
     * setupHorizontalLines
     * @description Setups click listener for Main View
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setupHorizontalLines() {
        binding.sHorizontal.hide()
        binding.sVertical.hide()
        binding.screen.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    val x = event.x
                    val y = event.y
                    // do something with x and y coordinates
                    touchableEvent(x, y)
                    true
                }

                else -> false
            }
        }

        setButtons(false)
    }

    private fun touchableEvent(x: Float, y: Float) {
        Log.d(
            "Box",
            "//Click Detected: x:$x y:$y"
        )



        when {

            (translateX != 0.0F) -> {
                this.translateY = binding.sHorizontal.translationY
                val x = translateX
                val y = translateY
                binding.sText.text = "3º Stage -> $x $y "
                thirdStage(x, y)
            }
            (workingX && !workingY) -> {
                workingX = false
                workingY = true
                translateX = binding.sVertical.translationX
                binding.sHorizontal.show()
                binding.sText.text = "2º Stage"
                secondStage(rectangleTop, rectangleBottom)
            }

            (!workingX && !workingY) -> {
                val process = processVisualBox(x, y)
                Log.d("Box", "Process: $process")
                //Working X Axis
                workingX = true
                workingY = false
                //translateY = 0.0F
                //translateX = process.rectangleLeft
                //binding.sVertical.translationX = process.rectangleLeft
                binding.sVertical.show()
                binding.canvas.drawVisualBox(
                    process.rectangleLeft,
                    process.rectangleTop,
                    process.rectangleRight,
                    process.rectangleBottom
                )
                binding.sText.text = "1º Stage"
                firstStage(process.rectangleLeft, process.rectangleRight)
            }
        }
    }

    private fun thirdStage(x: Float, y: Float) {
        Log.d("Debug", "//Starting 3º Stage... $x $y")
        simulateTouch(x, y)
        workingY = false
        workingX = false
        this.animateX.cancel()
        this.animateY.cancel()
        translateY = 0.0F
        translateX = 0.0F
    }

    /**
     * secondStage
     * @description Call animation on sHorizontal View
     * @param from starting Y
     * @param to ending Y
     *
     * @see touchableEvent
     */
    private fun secondStage(from: Float, to: Float) {
        Log.d("Debug", "//Starting 2º Stage...")
        this.animateX.pause()
        this.animateY = animateViewFromTopToBottom(
            binding.sHorizontal,
            duration,
            from,
            to,
            LinearInterpolator()
        ) {
            Log.d("Animation", "Animation Y ended")
            workingY = false
            binding.canvas.clearRectangle()
            binding.sVertical.hide()
            binding.sHorizontal.hide()
        }
        this.animateY.start()
    }

    /**
     * firstStage
     * @description Call animation on sVertical View
     * @param from starting X
     * @param to ending X
     *
     * @see touchableEvent
     */
    private fun firstStage(from: Float, to: Float) {
        Log.d("Debug", "//Starting 1º Stage...")
        this.animateX=animateViewFromLeftToRight(
            binding.sVertical,
            duration,
            from,
            to,
            LinearInterpolator()
        ) {
            Log.d("Animation", "Animation X ended")
            workingX = false
            binding.canvas.clearRectangle()
            binding.sVertical.hide()
        }
        this.animateX.start()
    }

    private fun processVisualBox(x: Float, y: Float, scale: Int = 2): RectangleData {
        //Screen Info
        val containerWidth = resources.displayMetrics.widthPixels.toFloat()
        val containerHeight = resources.displayMetrics.heightPixels.toFloat()

        //Center coordinates
        val centerX = x - ((containerWidth / scale) / 2)
        val centerY = y - ((containerHeight / scale) / 2)

        //Possible Max
        val maxX = containerWidth - (containerWidth / scale)
        val maxY = containerHeight - (containerHeight / scale)

        //Bounded Values
        val boundedX = min(max(centerX, 0f), maxX)
        val boundedY = min(max(centerY, 0f), maxY)

        val left = boundedX
        val top = boundedY
        val right = boundedX + (containerWidth / scale)
        val bottom = boundedY + (containerHeight / scale)

        //Later debug if needed
        this.rectangleLeft = left
        this.rectangleTop = top
        this.rectangleRight = right
        this.rectangleBottom = bottom
        this.isRectangleVisible = true

        return RectangleData(
            isRectangleVisible,
            rectangleLeft,
            rectangleTop,
            rectangleRight,
            rectangleBottom
        )
    }

    private fun setButtons(clickable: Boolean = false) {
        binding.btnTopLeft.prepButton(clickable)
        binding.btnTopMid.prepButton(clickable)
        binding.btnTopRight.prepButton(clickable)
        binding.btnBotLeft.prepButton(clickable)
        binding.btnBotMid.prepButton(clickable)
        binding.btnBotRight.prepButton(clickable)
    }

    private fun Button.prepButton(isClickable: Boolean=false) {
        this.setOnClickListener { setMessage(this.text.toString()) } //callback
        this.isClickable = isClickable
        this.background = null //remove ripple effect
    }

    private fun setMessage(input: String) {
        Log.d("Debug", "//Input Received $input")
        binding.sResult.text = input
    }

    fun View.hide() {
        this.visibility = View.GONE
    }

    fun View.show() {
        this.visibility = View.VISIBLE
    }

    /**
     * animateViewFromLeftToRight
     * @description Generic Animation from the left to the right, on a X axis
     * @param view ID of a View to Manipulate
     * @param duration Duration of the animation in ms
     * @param from Starting Value for animation (Float)
     * @param to Ending Value for animation (Float)
     * @param onAnimationEnd Callback once Animation ends
     * @author Eduardo Faria
     *
     * @see firstStage
     */
    private fun animateViewFromLeftToRight(
        view: View,
        duration: Long,
        from: Float = 0.0F,
        to: Float = 0.0F,
        easing: TimeInterpolator = AccelerateDecelerateInterpolator(),
        onAnimationEnd: () -> Unit
    ): ValueAnimator {
        //If i provide a to, i dont need to calculate default
        val start = if (from != 0.0F) from else 0.0F
        val end = if (to != 0.0F) to else resources.displayMetrics.heightPixels.toFloat()
        val animator = ValueAnimator.ofFloat(start, end)
        animator.duration = duration
        animator.interpolator = easing
        animator.addUpdateListener {
            val value = it.animatedValue as Float
            view.translationX = value

        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onAnimationEnd()
            }
        })
        return animator
    }

    /**
     * animateViewFromTopToBottom
     * @description Generic Animation from the top to the bottom, on a Y axis
     * @param view ID of a View to Manipulate
     * @param duration Duration of the animation in ms
     * @param from Starting Value for animation (Float)
     * @param to Ending Value for animation (Float)
     * @param onAnimationEnd Callback once Animation ends
     *
     * @author Eduardo Faria
     * @see secondStage
     */
    private fun animateViewFromTopToBottom(
        view: View,
        duration: Long,
        from: Float = 0.0F,
        to: Float = 0.0F,
        easing: TimeInterpolator = AccelerateDecelerateInterpolator(),
        onAnimationEnd: () -> Unit
    ): ValueAnimator {
        //If i provide a to, i dont need to calculate default
        val start = if (from != 0.0F) from else 0.0F
        val end = if (to != 0.0F) to else resources.displayMetrics.heightPixels.toFloat()
        val animator = ValueAnimator.ofFloat(start, end)
        animator.duration = duration
        animator.interpolator = easing
        animator.addUpdateListener {
            val value = it.animatedValue as Float
            view.translationY = value

        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onAnimationEnd()
            }
        })
        return animator
        //animator.start()
    }

    private fun simulateTouch(x: Float, y: Float) {
        //Enabling Buttons
        binding.btnTopLeft.prepButton(true)
        binding.btnTopMid.prepButton(true)
        binding.btnTopRight.prepButton(true)
        binding.btnBotLeft.prepButton(true)
        binding.btnBotMid.prepButton(true)
        binding.btnBotRight.prepButton(true)

        Log.d("Debug", "//simulateTouch at $x $y")
        binding.root.isClickable = false
        val down = SystemClock.uptimeMillis()
        var event = SystemClock.uptimeMillis() + 50

        val pressDown = MotionEvent.obtain(
            down, event, MotionEvent.ACTION_DOWN, x, y, 0
        )
        binding.root.dispatchTouchEvent(pressDown)

        event = SystemClock.uptimeMillis() + 50

        val pressUp = MotionEvent.obtain(
            down, event, MotionEvent.ACTION_UP, x, y, 0
        )

        binding.root.dispatchTouchEvent(pressUp)
        //Disabling Buttons
        binding.btnTopLeft.prepButton()
        binding.btnTopMid.prepButton()
        binding.btnTopRight.prepButton()
        binding.btnBotLeft.prepButton()
        binding.btnBotMid.prepButton()
        binding.btnBotRight.prepButton()
        binding.root.isClickable = true
    }

}
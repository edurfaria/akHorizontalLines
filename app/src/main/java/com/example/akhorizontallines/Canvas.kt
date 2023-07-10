package com.example.akhorizontallines

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.math.max
import kotlin.math.min

class Canvas(context: Context, attrs: AttributeSet) : View(context, attrs) {
    // Coordinates for the dot (Circle)
    private var x = 0.0F
    private var y = 0.0F
    private val radius = 10F

    //Vars for Rectangle
    private var isRectangleVisible = false

    private var rectangleLeft = 0F
    private var rectangleTop = 0F
    private var rectangleRight = 0F
    private var rectangleBottom = 0F


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val paint = Paint().apply {
            color = Color.RED
            style = Paint.Style.FILL
        }

        val rectanglePaint = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 5F
        }

        if (isRectangleVisible) {
            Log.d("Debug", "//Re-Drawing...")

            canvas?.drawCircle(x, y, radius, paint)

            canvas?.drawRect(
                rectangleLeft,
                rectangleTop,
                rectangleRight,
                rectangleBottom,
                rectanglePaint
            )
        }
    }

    fun updatePosition(x: Float, y: Float) {
        this.x = x
        this.y = y
        invalidate() // Invalidate the view to trigger a redraw
    }

    fun drawRectangle(x: Float, y: Float, scale: Int = 2) {
        val containerWidth = width.toFloat()
        val containerHeight = height.toFloat()
        Log.d("Box", "width:$containerWidth - height:$containerHeight")

        val centerX = x - ((containerWidth / scale) / 2)
        val centerY = y - ((containerHeight / scale) / 2)

        val maxX = containerWidth - (containerWidth / scale)
        val maxY = containerHeight - (containerHeight / scale)

        val boundedX = min(max(centerX, 0f), maxX)
        val boundedY = min(max(centerY, 0f), maxY)

        val left = boundedX
        val top = boundedY
        val right = boundedX + (containerWidth / scale)
        val bottom = boundedY + (containerHeight / scale)

        this.x = x
        this.y = y
        this.rectangleLeft = left
        this.rectangleTop = top
        this.rectangleRight = right
        this.rectangleBottom = bottom
        this.isRectangleVisible = true
        invalidate() // Invalidate the view to trigger a redraw
    }

    fun clearRectangle() {
        this.isRectangleVisible = false
        invalidate() // Invalidate the view to trigger a redraw
    }


}
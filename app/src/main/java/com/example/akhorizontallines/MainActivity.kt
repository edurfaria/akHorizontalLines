package com.example.akhorizontallines

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import com.example.akhorizontallines.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

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
        binding.screen.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    val x = event.x
                    val y = event.y
                    // do something with x and y coordinates
                    binding.canvas.drawRectangle(x, y)
                    true
                }

                else -> false
            }
        }

        setButtons(false)
    }

    private fun setButtons(clickable: Boolean = false) {
        binding.btnTopLeft.prepButton(clickable)
        binding.btnTopMid.prepButton(clickable)
        binding.btnTopRight.prepButton(clickable)
        binding.btnBotLeft.prepButton(clickable)
        binding.btnBotMid.prepButton(clickable)
        binding.btnBotRight.prepButton(clickable)
    }

    private fun Button.prepButton(isClickable: Boolean) {
        this.setOnClickListener { setMessage(this.text.toString()) } //callback
        this.isClickable = isClickable
        this.background = null //remove ripple effect
    }

    private fun setMessage(input: String) {
        Log.d("Debug", "//Input Received $input")
        binding.sResult.text = input
    }
}
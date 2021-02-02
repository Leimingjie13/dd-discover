package com.example.doordashproject

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout

class TouchPassthroughLayout(context: Context, attrs: AttributeSet)
    : ConstraintLayout(context, attrs) {

    val interceptTouchListeners = mutableListOf<OnTouchListener>()

    override fun onInterceptTouchEvent(event: MotionEvent) : Boolean {
        interceptTouchListeners.forEach { it.onTouch(this, event) }
        return false
    }
}
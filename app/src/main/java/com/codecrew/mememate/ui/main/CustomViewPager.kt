package com.codecrew.mememate.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import java.util.concurrent.atomic.AtomicBoolean

// (MJ) Custom ViewPager to create enable and disable touch feature (to not interfere with meme swipe)
class CustomViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {

    private val touchesAllowed = AtomicBoolean()

    private fun touchesAllowed(): Boolean {
        return touchesAllowed.get()
    }

    fun enableTouches() {
        touchesAllowed.set(true)
    }

    fun disableTouches() {
        touchesAllowed.set(false)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return if (touchesAllowed()) {
            super.onInterceptTouchEvent(ev)
        } else {
            if (ev.actionMasked == MotionEvent.ACTION_MOVE) {
            } else {
                if (super.onInterceptTouchEvent(ev)) {
                    super.onTouchEvent(ev)
                }
            }
            false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return if (touchesAllowed()) {
            super.onTouchEvent(ev)
        } else {
            ev.actionMasked != MotionEvent.ACTION_MOVE && super.onTouchEvent(ev)
        }
    }
}
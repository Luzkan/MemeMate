package com.codecrew.mememate.ui.main

import android.content.Context
import android.support.v4.view.MotionEventCompat
import android.view.MotionEvent
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import java.util.concurrent.atomic.AtomicBoolean

class CustomViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {
    private val enabled: Boolean = false

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
        if (touchesAllowed()) {
            return super.onInterceptTouchEvent(ev)
        } else {
            if (MotionEventCompat.getActionMasked(ev) == MotionEvent.ACTION_MOVE) {
                // ignore move action
            } else {
                if (super.onInterceptTouchEvent(ev)) {
                    super.onTouchEvent(ev)
                }
            }
            return false
        }
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return if (touchesAllowed()) {
            super.onTouchEvent(ev)
        } else {
            MotionEventCompat.getActionMasked(ev) != MotionEvent.ACTION_MOVE && super.onTouchEvent(ev)
        }
    }
}
package com.aldieemaulana.take.activity.base

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import io.reactivex.disposables.Disposable

/**
 * Created by Al on 26/06/18 for Cermati
 */

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    open val context : Context = this
    open var disposable : Disposable? = null

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

}
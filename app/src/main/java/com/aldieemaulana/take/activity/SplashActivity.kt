package com.aldieemaulana.take.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.aldieemaulana.take.activity.base.BaseActivity
import com.aldieemaulana.take.R

/**
 * Created by Al on 26/06/18 for Cermati
 */

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        handler(1500)
    }

    private fun handler(DELAY_TIME: Long) {
        val mHandler = Handler()
        val mRunnable = Runnable {
            startActivity(Intent(context, MainActivity::class.java))
            finish()
        }

        mHandler.postDelayed(mRunnable, DELAY_TIME)
    }
}
